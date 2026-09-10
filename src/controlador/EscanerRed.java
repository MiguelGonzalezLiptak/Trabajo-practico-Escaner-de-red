package controlador;

//import modelo.Equipo;
//import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class EscanerRed {

    public boolean esIpValida(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String[] partes = ip.split("\\.");
        if (partes.length != 4) {
            return false;
        }
        for (String parte : partes) {
            try {
                int numero = Integer.parseInt(parte);
                if (numero < 0 || numero > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private long ipANumero(String ip) {
        String[] partes = ip.split("\\.");
    
        long n1 = Integer.parseInt(partes[0]);
        long n2 = Integer.parseInt(partes[1]);
        long n3 = Integer.parseInt(partes[2]);
        long n4 = Integer.parseInt(partes[3]);
        return (n1 * 256 * 256 * 256) + (n2 * 256 * 256) + (n3 * 256) + n4;
    }

    private String numeroAIp(long numero) {
        long parte1 = (numero / (256 * 256 * 256)) % 256;
        long parte2 = (numero / (256 * 256)) % 256;
        long parte3 = (numero / 256) % 256;
        long parte4 = numero % 256;
        return parte1 + "." + parte2 + "." + parte3 + "." + parte4;
}

    public List<String> generarRangoIps(String ipInicio, String ipFin) {
        List<String> listaIps = new ArrayList<>();
        long inicio = ipANumero(ipInicio);
        long fin = ipANumero(ipFin);
        if (inicio <= fin) {
            for (long i = inicio; i <= fin; i++) {
                listaIps.add(numeroAIp(i));
            }
        }
        return listaIps;
    }

}