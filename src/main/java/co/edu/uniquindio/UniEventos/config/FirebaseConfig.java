package co.edu.uniquindio.UniEventos.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import java.io.IOException;


@Configuration
public class  FirebaseConfig {


    @Bean
    public FirebaseApp intializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(
                "src/main/resources/uniquindio-36aa1-firebase-adminsdk-5vup3-bbd2742635.json"
        );


        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("uniquindio-36aa1.appspot.com")
                .build();


        if(FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }


        return null;
    }


}
