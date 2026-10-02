import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiGUI {
    private JPanel mainPanel;
    private JLabel id;
    private JTextField campoId;
    private JTextArea areaHabilidades;
    private JTextField campoNombre;
    private JTextField campoPeso;
    private JTextField campoAltura;
    private JTextField campoHp;
    private JTextField campoAtk;
    private JLabel textoImagen;
    private JTextField campoDef;
    private JTextField campoAtkEspecial;
    private JTextField campoDefEspecial;
    private JTextField campoVelocidad;

    public PokeApiGUI() {
        campoNombre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            consultarPokemon();
            }
        });
    }

    public void consultarPokemon(){
        try
        {
            String nombrePokemon = campoNombre.getText();
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://pokeapi.co/api/v2/pokemon/"+nombrePokemon )).build();
            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                JSONObject json = new JSONObject(response.body());
                String habilidades = "";
                String stats = "";

                campoId.setText(String.valueOf(json.getInt("id")));

                campoNombre.setText(String.valueOf(json.getString("name")));

                campoPeso.setText(String.valueOf(json.getInt("weight")));

                campoAltura.setText(String.valueOf(json.getInt("height")));

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

                //System.out.println("ID Pokemon: "+json.getInt("id"));



                //Vamos a ingresar a un array

                json.getJSONArray("abilities").forEach(habilidad ->
                {

                    //Accedemos al objeto
                    JSONObject abilityJson = (JSONObject) habilidad;
                    JSONObject abilityName = abilityJson.getJSONObject("ability");


                    areaHabilidades.append(abilityName.getString("name")+"\n");

                    System.out.println("Nombre habilidad: "+abilityName.getString("name"));
                });

                json.getJSONArray("stats").forEach(estadisticas ->
                {
                    JSONObject statsJson = (JSONObject) estadisticas;
                    System.out.println("Stat: "+statsJson.getInt("base_stat"));
                    JSONObject statName = statsJson.getJSONObject("stat");

                    String nombre =statName.getString("name");
                    int valor = statsJson.getInt("base_stat");

                    if (nombre.equals("hp"))
                        campoHp.setText(String.valueOf(valor));
                    else if(nombre.equals("attack"))
                        campoAtk.setText(String.valueOf(valor));
                    else if (nombre.equals("defense"))
                        campoDef.setText(String.valueOf(valor));
                    else if (nombre.equals("special-attack"))
                        campoAtkEspecial.setText(String.valueOf(valor));
                    else if (nombre.equals("special-defense"))
                        campoDefEspecial.setText(String.valueOf(valor));
                    else if (nombre.equals("speed"))
                        campoVelocidad.setText(String.valueOf(valor));




                    System.out.println("Nombre Estadistica: "+statName.getString("name"));

                });

                JSONObject foto = json.getJSONObject("sprites");

                try {
                    java.net.URL urlImagen = new java.net.URL(foto.getString("front_default"));
                    ImageIcon icono = new ImageIcon(urlImagen);
                    Image image = icono.getImage().getScaledInstance(150,150,Image.SCALE_DEFAULT);
                    textoImagen.setText("");
                    textoImagen.setIcon(new ImageIcon(image));

                } catch (Exception e) {
                    e.printStackTrace();
                    textoImagen.setText("No se pudo cargar la imagen");
                }
                String urlFoto = foto.getString("front_default");

                ImageIcon image = new ImageIcon(new URL(urlFoto));



                System.out.println(foto.getString("front_default"));

                System.out.println("\n Sonido: ");
                System.out.println(json.getJSONObject("cries").getString("latest"));




            }else {
                JOptionPane.showMessageDialog(null,"El pokemon no existe");
            }



        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PokeApiGUI");
        frame.setContentPane(new PokeApiGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400,750);
        frame.setLocation(600,50);

    }
}
