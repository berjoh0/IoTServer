/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver;

import com.google.gson.JsonObject;
import iotserver.request.HTTPRequest;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author johanbergman
 */
public class IoTMappings extends HashMap {

    public IoTMappings(JsonObject mappings) {
        for (Iterator it = mappings.keySet().iterator(); it.hasNext();) {
            String mapp_key = it.next().toString();
            JsonObject mapp_value = mappings.getAsJsonObject(mapp_key);

            if (mapp_value.has("path")) {
                this.put(mapp_key, new Mapping(mapp_key, mapp_value.get("path").getAsString(), mapp_value.get("defaultPage").getAsString(), Mapping.PATH));
            } else if (mapp_value.has("package")) {
                this.put(mapp_key, new Mapping(mapp_key, mapp_value.get("package").getAsString(), "", Mapping.PACKAGE));
            } else if (mapp_value.has("class")) {
                this.put(mapp_key, new Mapping(mapp_key, mapp_value.get("class").getAsString(), "", Mapping.CLASS));
            }
        }
    }

    public Mapping getMapping(HTTPRequest httpRequest) {
        String urlPart0 = httpRequest.getUrlParts()[0];

        if (this.containsKey(urlPart0)) {
            return (Mapping) this.get(urlPart0);
        } else if (this.containsKey("")) {
            return (Mapping) this.get("");
        } else {
            return null;
        }
    }

    public class Mapping {

        private String mapped_key;
        private String mapped_path;
        private String mapped_defaultPage;
        private int mapped_type;

        public static final int PATH = 0;
        public static final int PACKAGE = 1;
        public static final int CLASS = 2;

        public Mapping(String mapped_key, String mapped_path, String mapped_defaultPage, int mapped_type) {
            this.mapped_key = mapped_key;
            this.mapped_type = mapped_type;
            this.mapped_path = mapped_path;
            this.mapped_defaultPage = mapped_defaultPage;
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

}
