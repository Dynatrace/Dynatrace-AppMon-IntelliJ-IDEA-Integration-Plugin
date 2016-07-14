package com.dynatrace.diagnostics.codelink;


import javax.xml.bind.annotation.*;

/**
 * Borrowed from Eclipse plugin source code.
 * REST response for codelink lookup which is sent from dT client to eclipse integration plugin
 *
 * @author michael.kumar
 *         Date: 12.10.2009
 */
@XmlRootElement(name = "codeLinkLookup")
public class CodeLinkLookupResponse {

    @XmlAttribute(name = "versionMatched")
    public boolean versionMatched;

    @XmlAttribute(name = "timedOut")
    public boolean timedOut;

    @XmlAttribute(name = "className")
    public String className;

    @XmlAttribute(name = "methodName")
    public String methodName;

    @XmlAttribute(name = "sessionId")
    public long sessionId;

    @XmlElementWrapper(name = "attributes")
    @XmlElements(@XmlElement(name = "attribute", type = String.class))
    public String[] arguments;

}