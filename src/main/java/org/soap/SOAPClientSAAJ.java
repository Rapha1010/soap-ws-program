package org.soap;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import jdk.internal.org.xml.sax.InputSource;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

public class SOAPClientSAAJ {
    public static String soapAction = "";
    public static String ActionString = "ADD_INTEGER";
    public static String soapEndpointUrl = "https://www.crcind.com:443/csp/samples/SOAP.Demo.cls";

    public static ArrayList<Integer> argsSoapMethod = new ArrayList<>();

    // SAAJ - SOAP Client Testing
    public static void main(String args[]) {
        userInterface();
    }

    public static void userInterface() {

        Scanner sc= new Scanner(System.in);
        int using = 1;

        while(using == 1) {
            argsSoapMethod.clear();
            System.out.println("Choose one of the options");
            System.out.println("    Type 1 to divide Integer,");
            System.out.println("    Type 2 to sum integer,");
            System.out.println("    Type 3 to find a person by id");
            System.out.print("Type : ");

            int op= sc.nextInt();

            if (op == 1) {
                ActionString = "DIVIDE_INTEGER";
                System.out.print("Type dividend : ");
                int a = sc.nextInt();
                System.out.println();
                System.out.print("Type divisor : ");
                int b = sc.nextInt();
                argsSoapMethod.add(a);
                argsSoapMethod.add(b);

                callSoapWebService();
            } else if (op == 2) {
                ActionString = "ADD_INTEGER";
                System.out.print("Type number 1 : ");
                int a = sc.nextInt();
                System.out.println();
                System.out.print("Type number 2 : ");
                int b = sc.nextInt();
                argsSoapMethod.add(a);
                argsSoapMethod.add(b);
                callSoapWebService();

            } else if (op == 3) {
                ActionString = "FIND_PERSON";
                System.out.print("Type ID : ");
                int a = sc.nextInt();
                System.out.println();
                argsSoapMethod.add(a);
                callSoapWebService();
            }

            System.out.print("Continue using 1 (yes) or 2 (no)  : ");
            using = sc.nextInt();
        }
    }

    private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = "myNamespace";
        String myNamespaceURI = "http://tempuri.org";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        switch (ActionString) {
            case "DIVIDE_INTEGER":
                divideInteger(soapBody, myNamespace);
            break;
            case "ADD_INTEGER":
                addInteger(soapBody, myNamespace);
            break;
            case "FIND_PERSON":
                findPerson(soapBody, myNamespace);
            break;
        }

    }

    public static void divideInteger(SOAPBody soapBody, String myNamespace) {

        soapAction = "http://tempuri.org/SOAP.Demo.DivideInteger";

        try {
            SOAPElement soapBodyElem = soapBody.addChildElement("DivideInteger", myNamespace);
            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("Arg1", myNamespace);
            soapBodyElem1.addTextNode(argsSoapMethod.get(0).toString());
            SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("Arg2", myNamespace);
            soapBodyElem2.addTextNode(String.valueOf(argsSoapMethod.get(1)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addInteger(SOAPBody soapBody, String myNamespace) {

        soapAction = "http://tempuri.org/SOAP.Demo.AddInteger";

        try {
            SOAPElement soapBodyElem = soapBody.addChildElement("AddInteger", myNamespace);
            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("Arg1", myNamespace);
            soapBodyElem1.addTextNode(argsSoapMethod.get(0).toString());
            SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("Arg2", myNamespace);
            soapBodyElem2.addTextNode(argsSoapMethod.get(1).toString());
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void findPerson(SOAPBody soapBody, String myNamespace) {

        soapAction = "http://tempuri.org/SOAP.Demo.FindPerson";

        try {
            SOAPElement soapBodyElem = soapBody.addChildElement("FindPerson", myNamespace);
            SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("id", myNamespace);
            soapBodyElem1.addTextNode(argsSoapMethod.get(0).toString());
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void callSoapWebService() {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());

            System.out.println(strMsg);

            System.out.println();

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        System.out.println(soapMessage.getSOAPBody());

        createSoapEnvelope(soapMessage);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

}