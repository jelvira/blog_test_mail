package es.fs.blog.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class TestEmail {

    public static final String MAIL_ADDRESS = "future.blog.test.acc.2016@gmail.com";
    public static final String MAIL_PASSWORD = "futuretestblog";

    public static final String SMTP_PORT = "587";
    public static final String SMTP_GMAIL_SERVER = "smtp.gmail.com";

    public static final String IMAPS_PROTOCOL = "imaps";
    public static final String IMAP_GMAIL_SERVER = "imap.gmail.com";

    public static void main(String[] args) {
        sendMail();
        readMail();
    }

    private static void readMail() {
        Properties mailServerProperties = new Properties();

        mailServerProperties.setProperty("mail.store.protocol", IMAPS_PROTOCOL);

        try {
            Session mailSession = Session.getInstance(mailServerProperties, null);
            Store store = mailSession.getStore();
            store.connect(IMAP_GMAIL_SERVER, MAIL_ADDRESS, MAIL_PASSWORD);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (int i = 0; i < messages.length; i++) {
                System.out.println("===============================================");
                Message mailMessage = messages[i];
                Address[] in = mailMessage.getFrom();

                for (Address address : in) {
                    System.out.println("FROM:" + address.toString());

                }
                String content = null;

                if (mailMessage.getContent() instanceof MimeMultipart) {
                    Multipart mp = (Multipart) mailMessage.getContent();
                    BodyPart bp = mp.getBodyPart(0);
                    if (bp.getContent() instanceof MimeMultipart) {
                        content = (String) ((MimeMultipart) bp.getContent()).getBodyPart(0).getContent();
                    } else {
                        content = (String) bp.getContent();
                    }
                } else {
                    content = (String) mailMessage.getContent();
                }

                System.out.println("READ? " + (mailMessage.isSet(Flags.Flag.SEEN) ? "yes" : "no"));
                System.out.println("SENT DATE:" + mailMessage.getSentDate());
                System.out.println("SUBJECT:" + mailMessage.getSubject());
                System.out.println("CONTENT:" + content);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendMail() {

        try {

            Properties mailServerProperties;
            Session mailSession;
            MimeMessage mailMessage;

            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.port", SMTP_PORT);
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");

            mailSession = Session.getDefaultInstance(mailServerProperties, null);
            mailMessage = new MimeMessage(mailSession);

            mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(MAIL_ADDRESS));

            mailMessage.setSubject("Sent from java");
            String emailBody = "I send this mail to myself. <br> From <i>java</i>. ";
            MimePart part = new MimeBodyPart();
            mailMessage.setContent(emailBody, "text/html");
            System.out.println("Mail Session has been created successfully..");

            Transport transport = mailSession.getTransport("smtp");

            transport.connect(SMTP_GMAIL_SERVER, MAIL_ADDRESS, MAIL_PASSWORD);
            transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
            transport.close();

            System.out.println("Mail sent");

        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
}