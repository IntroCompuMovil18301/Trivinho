package javeriana.compumovil.tcp.trivinho;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;

public class ReservaTerminada extends IntentService {

    public ReservaTerminada() {
        super("ReservaTerminada");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
// Trabajo que debe hacer el servicio
// Por ahora solo esperar 5 segundos
        try {
            Thread.sleep(5000);
            Log.i("HECHO", "Servicio en ejecución" );
        } catch (InterruptedException e) {
// Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
}
