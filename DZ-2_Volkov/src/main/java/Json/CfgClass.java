package Json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cfg")

public class CfgClass {

    @XmlElement
    private String agentName;

    @Override
    public String toString() {
        return "CFGClass{" + "agentName = '" + agentName + '\'' + '}';
    }
}
