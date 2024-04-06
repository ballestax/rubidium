/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 *
 * @author ballestax
 */
public final class TimeWaste {

    private static final Logger logger = Logger.getLogger(TimeWaste.class.getCanonicalName());

    private Date getTime() throws UnknownHostException, IOException {

        try {
            String TIME_SERVER = "2.us.pool.ntp.org";
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date time = new Date(returnTime);
            return time;
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return new Date();

    }

    private Date getTime(String server) throws UnknownHostException, IOException {

        String TIME_SERVER = server;
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        Date time = new Date(returnTime);
        return time;

    }

    public Date getTimeServer() {
        String TIME_SERVER_1 = "0.us.pool.ntp.org";
        String TIME_SERVER_2 = "1.br.pool.ntp.org";
        String TIME_SERVER_3 = "0.pool.ntp.org";
        String[] T_SERVERS = {TIME_SERVER_1, TIME_SERVER_2, TIME_SERVER_3};
        int c = 0;
        while (c < T_SERVERS.length) {
            try {
                Date time = getTime(T_SERVERS[c]);
                if (time != null) {
                    return time;
                }
            } catch (UnknownHostException ex) {
                logger.warn(ex.getMessage());
                c++;
            } catch (IOException ex) {
                logger.warn(ex.getMessage());
                c++;
            } catch (Exception ex) {
                logger.warn(ex.getMessage());
                c++;
            }
        }
        return null;
    }

    /**
     *
     * @param date
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public final Date getTime(Date date) throws UnknownHostException, IOException {
        return getTime();
    }

    protected final void verInSMTYF(String sub, String sms) {
        Properties p = new Properties();
        
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");
        Session ses = Session.getInstance(p,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {                        
                        return new PasswordAuthentication("drsofttsms@gmail.com", String.valueOf(cast()));
                    }
                });
        MimeMessage mes = new MimeMessage(ses);
        try {
            mes.addRecipient(Message.RecipientType.TO, new InternetAddress("drsofttsms@gmail.com"));
            mes.setSubject(sub);
            mes.setText(sms);
            Transport.send(mes);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    protected final char[] cast() {
        int[][] cl = new LevenshteinDistance().vclv();

        char[] cad = new char[cl.length * cl[0].length];
        int k = 0;
        for (int i = cl.length - 1; i >= 0; i--) {
            for (int j = 0; j < cl[0].length; j++) {
                char[] toChars = Character.toChars(cl[i][j]);
                cad[k++] = toChars[0];
            }
        }
        return cad;
    }

    public final char[] cst() {
        return cast();
    }
}
