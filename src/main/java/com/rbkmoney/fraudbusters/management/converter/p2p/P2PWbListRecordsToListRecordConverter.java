package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.fraudbusters.management.domain.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface P2PWbListRecordsToListRecordConverter {

    P2pListRecord destinationToSource(P2pWbListRecords destination);
}
