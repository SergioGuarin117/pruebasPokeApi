import org.json.JSONArray;
import org.json.JSONObject;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ServiceLoader;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.spi.FormatConversionProvider;

public class pokeApi {

    private String urlSonidoPokemon;

    public void consultarPokemon(String nombrePokemon, JLabel resultado, JLabel imagen){
        try
        {

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://pokeapi.co/api/v2/pokemon/"+nombrePokemon)).build();
            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                JSONObject json = new JSONObject(response.body());
                String habilidades = "";
                String stats = "";

                for (Object item : json.getJSONArray("abilities")){
                    JSONObject habilidad = (JSONObject) item;
                    JSONObject datosHabilidad = habilidad.getJSONObject("ability");
                    if (!habilidades.isEmpty()){
                        habilidades += ", ";
                    }

                    habilidades += datosHabilidad.getString("name");
                }

                for (Object item : json.getJSONArray("stats")){
                    JSONObject estadistica = (JSONObject) item;
                    JSONObject datosEstadisticas = estadistica.getJSONObject("stat");
                    if (!stats.isEmpty()){
                        stats += "<br>";
                    }

                    stats += datosEstadisticas.getString("name")
                            +": "+ estadistica.getInt("base_stat");
                }

                System.out.println("ID Pokemon: "+json.getInt("id"));
                resultado.setText("<html>Nombre: "+json.getString("name")+"<br>ID Pokemon: "+ json.getInt("id")
                +"<br>Peso: "+json.getInt("weight")+"Kg<br>Altura: "+json.getInt("height")+"Cm"
                +"<br>Habilidades: "+ habilidades + "<br>Estadisticas: " + stats);
                System.out.println("Nombre: "+json.getString("name"));
                System.out.println("Peso: "+json.getInt("weight"));
                System.out.println("Altura: "+json.getInt("height"));

                //Vamos a ingresar a un array

                json.getJSONArray("abilities").forEach(habilidad ->
                {
                 //Accedemos al objeto
                 JSONObject abilityJson = (JSONObject) habilidad;
                 JSONObject abilityName = abilityJson.getJSONObject("ability");

                    System.out.println("Nombre habilidad: "+abilityName.getString("name"));
                });

                json.getJSONArray("stats").forEach(estadisticas ->
                {
                    JSONObject statsJson = (JSONObject) estadisticas;
                    System.out.println("Stat: "+statsJson.getInt("base_stat"));
                    JSONObject statName = statsJson.getJSONObject("stat");

                    System.out.println("Nombre Estadistica: "+statName.getString("name"));

                });

                JSONObject foto = json.getJSONObject("sprites");
                String urlFoto = foto.getString("front_default");

                ImageIcon image = new ImageIcon(new URL(urlFoto));
                imagen.setIcon(image);


                System.out.println(foto.getString("front_default"));

                System.out.println("\n Sonido: ");
                System.out.println(json.getJSONObject("cries").getString("latest"));

                JSONObject sonidoPokemon = json.getJSONObject("cries");
                urlSonidoPokemon = sonidoPokemon.getString("latest");



            }else {
                JOptionPane.showMessageDialog(null,"El pokemon no existe");
            }



        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    static void main() {
        pokeApi aplicacion = new pokeApi();
        SwingUtilities.invokeLater(()->{
            JFrame ventana = new JFrame("La Pokédex");
            ventana.setSize(700,450);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout(10,10));
            JPanel panelBusqueda = new JPanel();
            JPanel panelDatos = new JPanel(new FlowLayout(FlowLayout.CENTER,50,10));


            JLabel resultado = new JLabel("Aqui aparecera el Pokemon");
            JLabel imagen = new JLabel();
            JTextField campoNombre = new JTextField(15);
            JButton botonBuscar = new JButton("Buscar");
            JButton botonSonido = new JButton("Sonido");


            botonBuscar.addActionListener(evento ->{
                    String nombre = campoNombre.getText();
                    aplicacion.consultarPokemon(nombre,resultado, imagen);
                    });


            botonSonido.addActionListener(evento ->{


            });


            panelBusqueda.add(campoNombre);
            panelBusqueda.add(botonBuscar);

            panelDatos.add(resultado);
            panelDatos.add(imagen);
            panelDatos.add(botonSonido);

            panel.add(panelBusqueda, BorderLayout.NORTH );
            panel.add(panelDatos, BorderLayout.CENTER);
            ventana.add(panel);
            ventana.setVisible(true);

        });
    }
}
