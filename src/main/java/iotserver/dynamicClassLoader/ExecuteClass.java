/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.dynamicClassLoader;

import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.http.HttpResponse;

/**
 *
 * @author johanbergman
 */
public class ExecuteClass {

    private Class<?> classToRun = null;
    private Object runClassObj = null;
    private Object[] arg = null;
    private Method mth = null;

    public HTTPResponse executePackage(String className, HTTPRequest httpRequest) {

        for (int i = 1; i < httpRequest.getUrlParts().length; i++) {
            className += ("." + httpRequest.getUrlParts()[i]);
        }

        return execute(className, httpRequest);
    }

    public HTTPResponse execute(String className, HTTPRequest httpRequest) {
        HTTPResponse httpResponse;

        ClassToRun ctr = new ClassToRun(className, "do" + httpRequest.getMethod().toUpperCase(), httpRequest);

        if (getMethod(ctr)) {
            httpResponse = invokeMethod(ctr);
            if (httpResponse == null) {

            }

        } else {
            httpResponse = new HTTPResponse();
            httpResponse.setReturnCode(404);
            httpResponse.setReturnMessage(
                    "class " + ctr.getClassToLoad() + " or method " + ctr.getMethodToRun() + " not found!!");
        }

        return httpResponse;
    }

    public boolean getMethod(ClassToRun ctr) {

        try {
            ClassLoader cloader = getClassLoader();

            if (cloader == null) {
                System.out.println("ClassLoader saknas");
                return false;
            }

            try {
                classToRun = cloader.loadClass(ctr.getClassToLoad());

            } catch (NoClassDefFoundError e) {
                // TODO: handle exception
                return false;
            }

            runClassObj = null;

            try {
                try {
                    Constructor<?> ct = classToRun.getConstructor();
                    runClassObj = ct.newInstance();
                } catch (InvocationTargetException e) {

                    // Answer:
                    e.getCause().printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                mth = classToRun.getMethod(ctr.getMethodToRun(), HTTPRequest.class);

            } catch (Exception e) {

                System.out.println("error loading class: " + ctr.getClassToLoad() + "/" + ctr.getMethodToRun());
                e.printStackTrace();
                return false;

            }

        } catch (Exception e) {
            System.out.println("ERRRR");
            e.printStackTrace();
            return false;

        }
        return true;
    }

    public HTTPResponse invokeMethod(ClassToRun ctr) {
        try {
            arg = new Object[] { ctr.getHttpRequestParameter() };
            HTTPResponse httpResponse = (HTTPResponse) mth.invoke(runClassObj, arg);
            return httpResponse;
        } catch (Exception e) {
            HTTPResponse httpResponse = new HTTPResponse();
            httpResponse.setReturnCode(405);
            httpResponse.setReturnMessage("Error executing " + runClassObj.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));

            httpResponse.setBody(baos.toByteArray());
            e.printStackTrace();
            return httpResponse;
        }
    }

    public ClassLoader getClassLoader() {
        ClassLoader cloader = null;
        try {
            {
                cloader = (ClassLoader) Thread.currentThread().getContextClassLoader();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloader;

    }

    public class ClassToRun {

        public ClassToRun(String classToLoad, String methodToRun, HTTPRequest httpRequestParameter) {
            this.classToLoad = classToLoad;
            this.methodToRun = methodToRun;
            this.httpRequestParameter = httpRequestParameter;
        }

        private String classToLoad;
        private String methodToRun;
        private HTTPRequest httpRequestParameter;

        /**
         * @return the classToLoad
         */
        public String getClassToLoad() {
            return classToLoad;
        }

        /**
         * @return the methodToRun
         */
        public String getMethodToRun() {
            return methodToRun;
        }

        /**
         * @return the httpRequestParameter
         */
        public HTTPRequest getHttpRequestParameter() {
            return httpRequestParameter;
        }
    }

}
