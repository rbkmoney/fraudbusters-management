package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.FraudInfo;
import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.fraudbusters.management.constant.CsvFraudPaymentFields;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVFraudPaymentParser implements CsvParser<FraudPayment> {

    public static final String TYPE = "text/csv";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String PAYMENT_RESOURCE = "payment_resource";
    public static final String RECURRENT = "recurrent";
    public static final String CUSTOMER = "customer";
    public static final String PAYER_TYPE = "payer_type";

    @Override
    public boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    @Override
    public List<FraudPayment> parse(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
            List<FraudPayment> fraudPayments = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                fraudPayments.add(mapFraudPayment(csvRecord));
            }

            return fraudPayments;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    private static FraudPayment mapFraudPayment(CSVRecord csvRecord) {
        return new FraudPayment()
                .setCost(new Cash()
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(csvRecord.get(CsvFraudPaymentFields.CUR)))
                        .setAmount(Long.parseLong(csvRecord.get(CsvFraudPaymentFields.AMOUNT))))
                .setEventTime(csvRecord.get(CsvFraudPaymentFields.EVENT_TIME))
                .setId(csvRecord.get(CsvFraudPaymentFields.PAYMENT))
                .setFraudInfo(new FraudInfo()
                        .setTempalteId(csvRecord.get(CsvFraudPaymentFields.CHECKED_TEMPLATE))
                        .setCheckStatus(csvRecord.get(CsvFraudPaymentFields.RESULT_STATUS))
                        .setType(csvRecord.get(CsvFraudPaymentFields.FRAUD_TYPE))
                )
                .setPayer(initPayer(csvRecord))
                .setReferenceInfo(ReferenceInfo.merchant_info(new MerchantInfo()
                        .setPartyId(csvRecord.get(CsvFraudPaymentFields.PARTY_ID))
                        .setShopId(csvRecord.get(CsvFraudPaymentFields.SHOP_ID))))
                .setRoute(new PaymentRoute()
                        .setProvider(new ProviderRef()
                                .setId(Integer.parseInt(csvRecord.get(CsvFraudPaymentFields.ROUTE_PROVIDER_ID))))
                        .setTerminal(new TerminalRef()
                                .setId(Integer.parseInt(csvRecord.get(CsvFraudPaymentFields.ROUTE_TERMINAL_ID)))))
                .setRrn(csvRecord.get(CsvFraudPaymentFields.TRX_ADDITIONAL_INFO_RRN));
    }

    private static Payer initPayer(CSVRecord csvRecord) {
        Payer payer;
        String maskedPan = csvRecord.get(CsvFraudPaymentFields.CARD);
        String lastDigist = maskedPan.substring(maskedPan.length() - 4);
        String bin = maskedPan.substring(0, maskedPan.length() - 4);
        switch (csvRecord.get(PAYER_TYPE)) {
            case PAYMENT_RESOURCE:
                payer = Payer.payment_resource(new PaymentResourcePayer()
                        .setContactInfo(initContactInfo(csvRecord))
                        .setResource(new DisposablePaymentResource()
                                .setClientInfo(new ClientInfo()
                                        .setFingerprint(csvRecord.get(CsvFraudPaymentFields.FINGERPRINT))
                                        .setIpAddress(csvRecord.get(CsvFraudPaymentFields.IP))
                                )
                                .setPaymentTool(initPaymentTool(csvRecord, lastDigist, bin)))
                );
                break;
            case RECURRENT:
                payer = Payer.recurrent(new RecurrentPayer()
                        .setContactInfo(initContactInfo(csvRecord))
                        .setRecurrentParent(new RecurrentParentPayment()
                                .setInvoiceId(UNKNOWN)
                                .setPaymentId(UNKNOWN))
                        .setPaymentTool(initPaymentTool(csvRecord, lastDigist, bin)));
                break;
            case CUSTOMER:
                payer = Payer.customer(new CustomerPayer()
                        .setCustomerId(UNKNOWN)
                        .setCustomerBindingId(UNKNOWN)
                        .setRecPaymentToolId(UNKNOWN)
                        .setContactInfo(initContactInfo(csvRecord))
                        .setPaymentTool(initPaymentTool(csvRecord, lastDigist, bin)));
                break;
            default:
                throw new RuntimeException("Unknown payer_type");
        }
        return payer;
    }

    private static ContactInfo initContactInfo(CSVRecord csvRecord) {
        return new ContactInfo()
                .setEmail(csvRecord.get("email"));
    }

    private static PaymentTool initPaymentTool(CSVRecord csvRecord, String lastDigist, String bin) {
        return PaymentTool.bank_card(new BankCard()
                .setLastDigits(lastDigist)
                .setBin(bin)
                .setToken(csvRecord.get("cardToken"))
                .setIssuerCountry(Residence.valueOf(csvRecord.get("bankCountry"))));
    }

}