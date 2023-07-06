package iotserver.mapping;

public class IoTMapping {

    private String mapped_key;
    private String mapped_path;
    private String mapped_defaultPage;
    private int mapped_type;

    public static final int PATH = 0;
    public static final int PACKAGE = 1;
    public static final int CLASS = 2;

    public IoTMapping(String mapped_key, String mapped_path, String mapped_defaultPage, int mapped_type) {
        this.mapped_key = mapped_key;
        this.mapped_type = mapped_type;
        this.mapped_path = mapped_path;
        this.mapped_defaultPage = mapped_defaultPage;
    }

    /**
     * @return the mapped_key
     */
    public String getMapped_key() {
        return mapped_key;
    }

    /**
     * @return the mapped_key
     */
    public String getMapped_defaultPage() {
        return mapped_defaultPage;
    }

    /**
     * @return the mapped_path
     */
    public String getMapped_path() {
        return mapped_path;
    }

    /**
     * @return the mapped_path
     */
    public String buildMapped_path(String fileName) {
        String retPath;
        if (!mapped_key.isEmpty()) {
            retPath = fileName.substring(mapped_key.length());
        } else {
            retPath = "/" + fileName;
        }

        if (retPath.isEmpty() || retPath.equals("/")) {
            retPath = "/" + mapped_defaultPage;
        }

        return mapped_path + retPath;
    }

    /**
     * @param mapped_path the mapped_path to set
     */
    public void setMapped_path(String mapped_path) {
        this.mapped_path = mapped_path;
    }

    /**
     * @return the mapped_type
     */
    public int getMapped_type() {
        return mapped_type;
    }

    /**
     * @param mapped_type the mapped_type to set
     */
    public void setMapped_type(int mapped_type) {
        this.mapped_type = mapped_type;
    }

}
