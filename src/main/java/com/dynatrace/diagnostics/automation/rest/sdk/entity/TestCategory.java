package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum(String.class)
@XmlType
public enum TestCategory {

    @XmlEnumValue("unit")
    UNIT,
    @XmlEnumValue("uidriven")
    UI_DRIVEN,
    @XmlEnumValue("performance")
    PERFORMANCE,
    @XmlEnumValue("webapi")
    WEB_API;
}
