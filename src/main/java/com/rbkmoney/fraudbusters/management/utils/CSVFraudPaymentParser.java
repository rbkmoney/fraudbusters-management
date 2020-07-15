package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.FraudInfo;
import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
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
public class CSVFraudPaymentParser implements CsvParser<FraudPayment>{

    public static final String TYPE = "text/csv";
    public static final String UNKNOWN = "UNKNOWN";

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
                                .setSymbolicCode(csvRecord.get("cur")))
                        .setAmount(Long.valueOf(csvRecord.get("amount"))))
                .setEventTime(csvRecord.get("eventTime"))
                .setId(csvRecord.get("payment"))
                .setFraudInfo(new FraudInfo()
                        .setTempalteId(csvRecord.get("checkedTemplate"))
                        .setCheckStatus(csvRecord.get("resultStatus"))
                        .setType(csvRecord.get("fraud_type"))
                )
                .setPayer(initPayer(csvRecord))
                .setReferenceInfo(ReferenceInfo.merchant_info(new MerchantInfo()
                        .setPartyId(csvRecord.get("partyId"))
                        .setShopId(csvRecord.get("shopId"))))
                .setRoute(new PaymentRoute()
                        .setProvider(new ProviderRef()
                                .setId(Integer.valueOf(csvRecord.get("route_provider_id"))))
                        .setTerminal(new TerminalRef()
                                .setId(Integer.valueOf(csvRecord.get("route_terminal_id")))))
                .setRrn(csvRecord.get("trx_additional_info_rrn"));
    }

    private static Payer initPayer(CSVRecord csvRecord) {
        Payer payer;
        String maskedPan = csvRecord.get("card");
        String lastDigist = maskedPan.substring(maskedPan.length() - 4);
        String bin = maskedPan.substring(0, maskedPan.length() - 4);
        switch (csvRecord.get("payer_type")) {
            case "payment_resource":
                payer = Payer.payment_resource(new PaymentResourcePayer()
                        .setContactInfo(initContactInfo(csvRecord))
                        .setResource(new DisposablePaymentResource()
                                .setClientInfo(new ClientInfo()
                                        .setFingerprint(csvRecord.get("fingerprint"))
                                        .setIpAddress(csvRecord.get("ip"))
                                )
                                .setPaymentTool(initPaymentTool(csvRecord, lastDigist, bin)))
                );
                break;
            case "recurrent":
                payer = Payer.recurrent(new RecurrentPayer()
                        .setContactInfo(initContactInfo(csvRecord))
                        .setRecurrentParent(new RecurrentParentPayment()
                                .setInvoiceId(UNKNOWN)
                                .setPaymentId(UNKNOWN))
                        .setPaymentTool(initPaymentTool(csvRecord, lastDigist, bin)));
                break;
            case "customer":
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