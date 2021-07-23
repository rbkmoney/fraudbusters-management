package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.converter.payment.GroupModelToGroupConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.GroupToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentGroupReferenceModelToGroupReferenceConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentGroupCommandService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentGroupReferenceCommandService;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsGroupsApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentGroupsResource implements PaymentsGroupsApi {

    private final PaymentGroupDao groupDao;
    private final PaymentGroupReferenceDao referenceDao;
    private final UserInfoService userInfoService;
    private final PaymentGroupReferenceCommandService paymentGroupReferenceService;
    private final PaymentGroupCommandService paymentGroupCommandService;
    private final GroupToCommandConverter groupToCommandConverter;
    private final PaymentGroupReferenceModelToGroupReferenceConverter referenceModelToGroupReferenceConverter;
    private final GroupModelToGroupConverter groupModelToGroupConverter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<GroupsResponse> filterGroups(@Valid String searchValue) {
        log.info("-> filterGroup initiator: {} searchValue: {}", userInfoService.getUserName(), searchValue);
        List<GroupModel> groupModels = groupDao.filterGroup(searchValue);
        log.info("filterGroup groupModels: {}", groupModels);
        return ResponseEntity.ok().body(new GroupsResponse()
                .result(groupModels.stream()
                        .map(groupModelToGroupConverter::convert)
                        .collect(Collectors.toList())
                ));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<GroupsReferencesResponse> filterGroupsReferences(@Valid String sortOrder,
                                                                           @Valid String searchValue,
                                                                           @Valid String sortBy,
                                                                           @Valid String sortFieldValue,
                                                                           @Valid Integer size, @Valid String lastId) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        log.info("filterReference idRegexp: {}", filterRequest.getSearchValue());
        List<PaymentGroupReferenceModel> listByTemplateId = referenceDao.filterReference(filterRequest);
        Integer count = referenceDao.countFilterReference(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(new GroupsReferencesResponse()
                .count(count)
                .result(listByTemplateId.stream()
                        .map(referenceModelToGroupReferenceConverter::convert)
                        .collect(Collectors.toList()))
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Group> getGroup(String id) {
        log.info("getGroupById initiator: {} groupId: {}", userInfoService.getUserName(), id);
        var groupModel = groupDao.getById(id);
        if (groupModel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(groupModelToGroupConverter.convert(groupModel));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertGroup(@Valid Group group) {
        String userName = userInfoService.getUserName();
        log.info("insertTemplate initiator: {} group: {}", userName, group);
        var command = groupToCommandConverter.convert(group);
        command = paymentGroupCommandService.initCreateCommand(command, userName);
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ListResponse> insertGroupReferences(String id, @Valid List<GroupReference> groupReference) {
        String userName = userInfoService.getUserName();
        log.info("insertReference initiator: {} referenceModels: {}", userName, groupReference);
        List<String> ids = groupReference.stream()
                .map(reference -> paymentGroupCommandService.convertReferenceModel(reference, id))
                .map(command -> paymentGroupCommandService.initCreateCommand(command, userName))
                .map(paymentGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(new ListResponse()
                .result(ids));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroup(String id) {
        String userName = userInfoService.getUserName();
        log.info("removeGroup initiator: {} id: {}", userName, id);
        var command = paymentGroupCommandService.initDeleteGroupReferenceCommand(id, userName);
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroupReference(String id, @NotNull @Valid String partyId,
                                                       @NotNull @Valid String shopId, @Valid String groupId) {
        String userName = userInfoService.getUserName();
        log.info("removeGroupReference initiator: {} groupId: {} partyId: {} shopId: {}",
                userName, groupId, partyId, shopId);
        var command = paymentGroupCommandService.initDeleteGroupReferenceCommand(partyId, shopId, groupId, userName);
        String resultId = paymentGroupReferenceService.sendCommandSync(command);
        log.info("removeGroupReference sendCommand id: {}", resultId);
        return ResponseEntity.ok().body(resultId);
    }

}
