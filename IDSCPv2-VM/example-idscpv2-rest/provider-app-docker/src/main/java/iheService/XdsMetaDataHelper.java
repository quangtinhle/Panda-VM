package iheService;

import de.fraunhofer.isst.health.ihe.commons.domain.Code;

public final class XdsMetaDataHelper {

    private XdsMetaDataHelper() { }

    public static Code getCode(String codesystem, String codesystemName, String value, String displayname) {
        Code code = new Code();
        code.setCodesystem(codesystem);
        code.setCodesystemName(codesystemName);
        code.setDisplayname(displayname);
        code.setValue(value);
        return code;
    }

}
