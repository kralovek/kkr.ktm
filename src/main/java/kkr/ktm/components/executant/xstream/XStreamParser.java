package kkr.ktm.components.executant.xstream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import kkr.ktm.components.executant.xstream.data.InputParameters;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.FunctionalException;
import kkr.ktm.exception.TechnicalException;

public class XStreamParser {
    private static XStreamParser instance = new XStreamParser();
    public static XStreamParser getInstance() {
        return instance;
    }
    private XStreamParser() {
    }

    public String formatXML(final Object pObject) {
        XStream xstream = new XStream(new DomDriver());

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        xstream.toXML(pObject, byteArrayOutputStream);

        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + byteArrayOutputStream.toString();

        return xml;
    }

    public InputParameters parseInputParameters(final String pSource) throws BaseException {
        final Object object = loadInputObject(pSource);
        if (object == null) {
            throw new FunctionalException("Xstream cannot instantiate the object " + InputParameters.class + " from the source");
        }
        if (!(object instanceof InputParameters)) {
            throw new FunctionalException("The object parsed from the source is not an instance of " + InputParameters.class);
        }
        final InputParameters inputParameters = (InputParameters) object;
        if (isEmpty(inputParameters.getMethodname())) {
            throw new FunctionalException("Parameter 'method' is not specified in the parsed source");
        }
        return inputParameters;
    }

    private Object loadInputObject(final String pSource) throws BaseException {
        XStream xstream = new XStream(new DomDriver());

        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(pSource.getBytes());
            Object object = xstream.fromXML(byteArrayInputStream);
            byteArrayInputStream.close();
            byteArrayInputStream = null;
            return object;
        } catch (final IOException ex) {
            throw new TechnicalException("Cannot load the XML from the source: " + pSource, ex);
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (final Exception ex2) {
                }
            }
        }
    }

    private boolean isEmpty(final String pValue) {
        return pValue == null || pValue.isEmpty();
    }
}
