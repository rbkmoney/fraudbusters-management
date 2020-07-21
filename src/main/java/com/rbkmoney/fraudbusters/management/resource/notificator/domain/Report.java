package com.rbkmoney.fraudbusters.management.resource.notificator.domain;

public class Report implements Serializable {

    private static final long serialVersionUID = -1994947356;

    private Long id;
    private LocalDateTime createdAt;
    private String notificationName;
    private String result;
    private ReportStatus status;

}