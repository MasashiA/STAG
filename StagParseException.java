
public class StagParseException extends StagException
{
    String message;
    public StagParseException(String message) {this.message = message;}
    public String toString() {return "Error: " + this.message;}
}
