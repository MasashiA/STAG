
public class StagCommandException extends StagException
{
    String message; 
    public StagCommandException() {}
    public StagCommandException(String str) {this.message = str;}
    public String toString() {return this.message;}
}
