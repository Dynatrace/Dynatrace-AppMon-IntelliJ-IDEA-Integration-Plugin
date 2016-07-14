package com.dynatrace.diagnostics.automation.rest.sdk.entity;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by krzysztof.necel on 2016-02-04.
 */
@XmlEnum(String.class)
@XmlType
public enum TestStatus {
    @XmlEnumValue("failed")
    FAILED,
    @XmlEnumValue("degraded")
    DEGRADED,
    @XmlEnumValue("volatile")
    VOLATILE,
    @XmlEnumValue("improved")
    IMPROVED,
    @XmlEnumValue("passed")
    PASSED,
    @XmlEnumValue("invalidated")
    INVALIDATED
}