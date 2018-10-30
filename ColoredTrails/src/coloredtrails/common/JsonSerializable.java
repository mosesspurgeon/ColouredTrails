package coloredtrails.common;

import org.json.simple.JsonObject;

/**
 * This interface is introduced to make any object that needs to be sent over
 * the wire JSON serializable
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public interface JsonSerializable {

    public JsonObject toJsonObject();
}
