package zadaci;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import model.Avion;
import model.Roba;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvionNit extends Thread {

    static Dao<Avion,Integer> avionDao;
    static Dao<Roba,Integer> robaDao;

    public static final Object sinhronizacija=new Object();

    public static boolean dozvoljenoPoletanje= true;

    private Avion avion;

    public AvionNit(){

    }
    public AvionNit(Avion avion){
        this.avion = avion;

    }

    @Override
    public void run() {
        System.out.println("Počinju provere za avion" + avion.getOznaka() +  " na početku izvršavanja");
        poletanje();

    }

    public void poletanje(){

        try {
            int period = (int)(Math.random() * 2000);
            this.sleep(period);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean dozvolaZaPoletanje;

        System.out.println("Avion" + avion.getOznaka() + "je spreman za poletanje i čeka dozvolu za poletanje");
        do{

            synchronized (AvionNit.sinhronizacija) {

                if (AvionNit.dozvoljenoPoletanje) {

                    AvionNit.dozvoljenoPoletanje = false;
                    dozvolaZaPoletanje = true;
                } else {
                    dozvolaZaPoletanje = false;
                }
            }
        } while (!dozvolaZaPoletanje);

        System.out.println("Avion" + avion.getOznaka() +"izlazi na pistu i poleće");

                try {

                    this.sleep(2000);
                    System.out.println("Avion "+ avion.getOznaka() +"je poleteo");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



        synchronized (AvionNit.sinhronizacija) {

            AvionNit.dozvoljenoPoletanje = true;
        }




    }

    public static void main(String[] args) {
        ConnectionSource connectionSource = null;

        try {
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:avionRoba.db");
            avionDao = DaoManager.createDao(connectionSource, Avion.class);
            robaDao = DaoManager.createDao(connectionSource, Roba.class);
            ArrayList<Thread> listaT = new ArrayList<Thread>();

        List<Avion> avioni= avionDao.queryForAll();
        for(Avion a:avioni) {

          AvionNit a1 = new AvionNit(a);
          a1.start();
          listaT.add(a1);

        }
        for(int i=0; i<listaT.size(); i++) {

            try {
                listaT.get(i).join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Svi avioni su poleteli!");
    }


}
