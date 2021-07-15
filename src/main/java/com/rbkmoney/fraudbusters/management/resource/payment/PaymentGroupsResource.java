package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.UserInfo;
import com.rbkmoney.fraudbusters.management.converter.p2p.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.GroupToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentGroupReferenceModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentGroupReferenceService;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
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
    private final PaymentGroupReferenceService paymentGroupReferenceService;
    private final GroupCommandService paymentGroupCommandService;
    private final GroupToCommandConverter groupToCommandConverter;
    private final PaymentGroupReferenceModelToCommandConverter groupReferenceToCommandConverter;


    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<GroupsResponse> filterGroups(@Valid String sortOrder, @Valid String searchValue,
                                                       @Valid String sortBy, @Valid String sortFieldValue,
                                                       @Valid Integer size) {
        log.info("filterGroup initiator: {} groupId: {}", userInfoService.getUserName(), searchValue);
        List<GroupModel> groupModels = groupDao.filterGroup(searchValue);
        return ResponseEntity.ok().body(new GroupsResponse()
                .result(groupModels.stream()
                        .map(this::convertGroup)
                        .collect(Collectors.toList())
                ));
    }

    private List<PriorityId> convertPriorityTemplates(List<PriorityIdModel> priorityTemplates) {
        return priorityTemplates.stream()
                .map(this::convertPriorityId)
                .collect(Collectors.toList());
    }

    private PriorityId convertPriorityId(PriorityIdModel priorityIdModel) {
        return new PriorityId()
                .id(priorityIdModel.getId())
                .priority(priorityIdModel.getPriority())
                .lastUpdateTime(DateTimeUtils.toDate(priorityIdModel.getLastUpdateTime()));
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
        return ResponseEntity.ok().body(
                new GroupsReferencesResponse()
                        .count(count)
                        .result(listByTemplateId.stream()
                                .map(this::convertGroupReference)
                                .collect(Collectors.toList()))
        );
    }

    private GroupReference convertGroupReference(PaymentGroupReferenceModel groupReferenceModel) {
        return new GroupReference()
                .id(groupReferenceModel.getId())
                .shopId(groupReferenceModel.getShopId())
                .partyId(groupReferenceModel.getPartyId())
                .groupId(groupReferenceModel.getGroupId())
                .lastUpdateDate(DateTimeUtils.toDate(groupReferenceModel.getLastUpdateDate()))
                .modifiedByUser(groupReferenceModel.getModifiedByUser());
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Group> getGroup(String id) {
        log.info("getGroupById initiator: {} groupId: {}", userInfoService.getUserName(), id);
        GroupModel groupModel = groupDao.getById(id);
        if (groupModel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(convertGroup(groupModel));
    }

    private Group convertGroup(GroupModel groupModel) {
        return new Group()
                .groupId(groupModel.getGroupId())
                .priorityTemplates(convertPriorityTemplates(groupModel.getPriorityTemplates()))
                .modifiedByUser(groupModel.getModifiedByUser());
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertGroup(@Valid Group group) {
        log.info("insertTemplate initiator: {} group: {}", userInfoService.getUserName(), group);
        Command command = groupToCommandConverter.convert(group);
        command.setCommandType(CommandType.CREATE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName()));
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ListResponse> insertGroupReferences(String id, @Valid List<GroupReference> groupReference) {
        log.info("insertReference initiator: {} referenceModels: {}", userInfoService.getUserName(),
                groupReference);
        List<String> ids = groupReference.stream()
                .map(reference -> convertReferenceModel(reference, id))
                .map(command -> {
                    command.setCommandType(CommandType.CREATE);
                    command.setUserInfo(new UserInfo()
                            .setUserId(userInfoService.getUserName()));
                    return command;
                })
                .map(paymentGroupReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(new ListResponse()
                .result(ids));
    }

    private Command convertReferenceModel(GroupReference groupReferenceModel, String groupId) {
        Command command = groupReferenceToCommandConverter.convert(groupReferenceModel);
        command.getCommandBody().getGroupReference().setGroupId(groupId);
        return command;
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroup(String id) {
        log.info("removeGroup initiator: {} id: {}", userInfoService.getUserName(), id);
        Command command = paymentGroupCommandService.createTemplateCommandById(id);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName()));
        String idMessage = paymentGroupCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeGroupReference(String id, @NotNull @Valid String partyId,
                                                       @NotNull @Valid String shopId, @Valid String groupId) {
        log.info("removeGroupReference initiator: {} groupId: {} partyId: {} shopId: {}",
                userInfoService.getUserName(), groupId, partyId, shopId);
        var groupReferenceModel = new GroupReference()
                .partyId(partyId)
                .shopId(shopId)
                .groupId(groupId);
        Command command = convertReferenceModel(groupReferenceModel, groupId);
        command.setCommandType(CommandType.DELETE);
        command.setUserInfo(new UserInfo()
                .setUserId(userInfoService.getUserName()));
        String resultId = paymentGroupReferenceService.sendCommandSync(command);
        log.info("removeGroupReference sendCommand id: {}", resultId);
        return ResponseEntity.ok().body(resultId);
    }
}
