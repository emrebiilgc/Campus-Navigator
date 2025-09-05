import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CampusNavigatorNetwork implements Serializable {
    static final long serialVersionUID = 11L;
    public double averageCartSpeed;
    public final double averageWalkingSpeed = 1000 / 6.0;
    public int numCartLines;
    public Station startPoint;
    public Station destinationPoint;
    public List<CartLine> lines;

    /**
     * Write the necessary Regular Expression to extract string constants from the fileContent
     * @return the result as String
     */
    public String getStringVar(String varName, String fileContent) {
        Pattern pattern = Pattern.compile("[\\t ]*" + varName + "\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(fileContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    /**
     * Write the necessary Regular Expression to extract floating point numbers from the fileContent
     * Your regular expression should support floating point numbers with an arbitrary number of
     * decimals or without any (e.g. 5, 5.2, 5.02, 5.0002, etc.).
     * @return the result as Double
     */
    public Double getDoubleVar(String varName, String fileContent) {
        Pattern pattern = Pattern.compile("[\\t ]*" + varName + "\\s*=\\s*([0-9]+(?:\\.[0-9]+)?)");
        Matcher matcer = pattern.matcher(fileContent);
        if (matcer.find()) {
            return Double.parseDouble(matcer.group(1));
        }
        return 0.0;
    }


    public int getIntVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract a Point object from the fileContent
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     * @return the result as a Point object
     */
    public Point getPointVar(String varName, String fileContent) {
        Point p = new Point(0, 0);
        Pattern pattern = Pattern.compile("\\s*" + Pattern.quote(varName) + "\\s*=\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)");
        Matcher matcher = pattern.matcher(fileContent);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new Point(x, y);
        }
        return p;
    }

    /**
     * Function to extract the cart lines from the fileContent by reading train line names and their 
     * respective stations.
     * @return List of CartLine instances
     */
    public List<CartLine> getCartLines(String fileContent) {
        List<CartLine> cartLines = new ArrayList<>();

        Pattern nmP = Pattern.compile("cart_line_name\\s*=\\s*\"([^\"]+)\"");
        Matcher nmM = nmP.matcher(fileContent);

        Pattern stBlkP = Pattern.compile("cart_line_stations\\s*=\\s*((?:\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*)+)");
        Matcher stBlkM = stBlkP.matcher(fileContent);

        int stBlkCnt = 0;

        if (!nmM.find() || !stBlkM.find()) {
            return cartLines;
        }

        do {
            String lnNm = nmM.group(1);
            String stBlk = stBlkM.group(1);

            List<Station> stList = new ArrayList<>();
            Pattern stP = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            Matcher stM = stP.matcher(stBlk);

            int matchCount = 0;
            while (stM.find()) {
                matchCount++;
            }

            stM = stP.matcher(stBlk);

            for (int idx = 1; idx <= matchCount; idx++) {
                stM.find();
                int xCrd = Integer.parseInt(stM.group(1));
                int yCrd = Integer.parseInt(stM.group(2));
                String stNm = lnNm + " Station " + idx;
                stList.add(new Station(new Point(xCrd, yCrd), stNm));
            }

            cartLines.add(new CartLine(lnNm, stList));
            stBlkCnt++;
        } while (nmM.find() && stBlkM.find());

        return cartLines;
    }





    /**
     * Function to populate the given instance variables of this class by calling the functions above.
     */
    public void readInput(String filePath) {
        try {
            Scanner scanner = new Scanner(new File(filePath));
            scanner.useDelimiter("\\Z");
            String fileContent = scanner.next();
            scanner.close();

            this.numCartLines = getIntVar("num_cart_lines", fileContent);
            this.averageCartSpeed = (getDoubleVar("average_cart_speed", fileContent) * 1000.0) / 60.0;
            this.startPoint = new Station(getPointVar("starting_point", fileContent), "Starting Point");
            this.destinationPoint = new Station(getPointVar("destination_point", fileContent), "Final Destination");
            this.lines = getCartLines(fileContent);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
