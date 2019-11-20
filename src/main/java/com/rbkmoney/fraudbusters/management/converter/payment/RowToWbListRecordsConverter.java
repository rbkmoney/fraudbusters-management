package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.PaymentId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import com.rbkmoney.fraudbusters.management.utils.RowUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RowToWbListRecordsConverter implements Converter<Row, WbListRecords> {

    private final RowUtilsService rowUtilsService;

    @Override
    public WbListRecords convert(Row destination) {
        WbListRecords wbListRecords = new WbListRecords();

        if (destination.isSetId() && destination.getId().isSetPaymentId()) {
            PaymentId paymentId = destination.getId().getPaymentId();
            wbListRecords.setPartyId(paymentId.getPartyId());
            wbListRecords.setShopId(paymentId.getShopId());

            wbListRecords.setListName(destination.getListName());
            wbListRecords.setListType(rowUtilsService.initListType(destination));
            wbListRecords.setValue(destination.getValue());
            wbListRecords.setRowInfo(rowUtilsService.initRowInfo(destination));

            return wbListRecords;
        }
        throw new UnknownEventException();
    }

}
