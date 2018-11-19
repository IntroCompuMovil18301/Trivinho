package javeriana.compumovil.tcp.trivinho;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.Huesped;
import javeriana.compumovil.tcp.trivinho.negocio.Reserva;


public class ReservaTerminada extends IntentService {

    private static int notificationId = 0013;

    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private FirebaseDatabase database;

    private FirebaseAuth mAuth;
    private FirebaseUser user;


    public ReservaTerminada() {
        super("ReservaTerminada");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
// Trabajo que debe hacer el servicio
// Por ahora solo esperar 5 segundos

        try {
            while(true) {
                database= FirebaseDatabase.getInstance();
                mAuth = FirebaseAuth.getInstance();

                user = mAuth.getCurrentUser();
                if (user!=null) {

                    myRef = database.getReference(Utils.getPathHuespedes() + user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Huesped huesped = dataSnapshot.getValue(Huesped.class);
                            if (huesped.getReservas() != null) {
                                int i=0;
                                for (Reserva reserva : huesped.getReservas()) {
                                    if (!reserva.isCalificada()){
                                        if (reservaTerminada(reserva)) {
                                            //se crea la notificacióon para la reservaaa
                                            Log.i("calificacion", String.valueOf(reserva.isCalificada()));
                                            mostrarNotificacion(reserva.getAlojamiento(), i);
                                        }
                                     }
                                     i++;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                Thread.sleep(10 * 1000 * 3600); //CADA 10 HORAS
                Log.i("HECHO", "Servicio en ejecución");

            }

        } catch (InterruptedException e) {
// Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    private boolean reservaTerminada(Reserva reserva){


            Calendar fechareservaFinal = Calendar.getInstance();
            fechareservaFinal.set(Calendar.YEAR, reserva.getAnioFinal());
            fechareservaFinal.set(Calendar.MONTH, reserva.getMesFinal()-1);
            fechareservaFinal.set(Calendar.DAY_OF_MONTH, reserva.getDiaFinal());
            Log.i("FECHA FINAL", fechareservaFinal.toString());


            Calendar fechaActual = Calendar.getInstance();

            if ( fechaActual.after(fechareservaFinal) ){
                Log.i("RETORNO", "true");
                return true;
            }

        return false;
    }
    private void mostrarNotificacion(String alojamiento, int i){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "APPTRIVINHO");
        mBuilder.setSmallIcon(R.drawable.notificacion);
        mBuilder.setContentTitle("Puedes calificar un alojamiento!");
        mBuilder.setContentText("Ha pasado la fecha de reserva de un alojamiento.");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(this, CalificarAlojamientoActivity.class);
        intent.putExtra("alojamiento", alojamiento);
        intent.putExtra("usuario", user.getDisplayName());
        intent.putExtra("usuarioId", user.getUid());
        intent.putExtra("i", i);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);


        notificationId ++;
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
// notificationId es un entero unico definido para cada notificacion que se lanza
        notificationManager.notify(notificationId, mBuilder.build());


    }
}
