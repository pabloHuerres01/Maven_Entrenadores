package org.example;

import org.example.DAO.AdminDAO;
import org.example.DAO.EntrenadorDAO;
import org.example.DAO.TorneoDAO;
import org.example.DTO.PaisDTO;
import org.example.DTO.TorneoDTO;


import org.example.clasesBase.AdminTorneo;
import org.example.clasesBase.Entrenador;
import org.example.clasesBase.Torneo;
import org.example.conexion.Conexion;
import org.example.controlFicheros.EscrituraFicheros;
import org.example.controlFicheros.LecturaFicheros;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public PaisDTO pais = new PaisDTO();
    public Scanner scanner = new Scanner(System.in);
    public LecturaFicheros lecturaFicheros = new LecturaFicheros();
    public static SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
    public EscrituraFicheros escrituraFicheros = new EscrituraFicheros();
    //public final  String file = "src/main/Files/Credenciales.txt";
    public boolean acceso;
    private ArrayList<Torneo> torneos = new ArrayList<>();
    private ArrayList<Entrenador> entrenadores = new ArrayList<>();

    private Conexion conexion = new Conexion();


    public static void main(String[] args) {
        Main main = new Main();
    }

    public Main (){
        login();


    }
     /*
      En el metodo login, metemos credenciales, y nos asignamos rol
     */


    public void login() {
        while (true) {
            System.out.print("Ingrese su nombre de usuario: ");
            String username = scanner.nextLine();

            System.out.print("Ingrese su contraseña: ");
            String password = scanner.nextLine();

            acceso = lecturaFicheros.controlLogIn(username, password);

            if (!acceso) {
                System.out.println("Credenciales incorrectas. \n" +
                        "Si desea salir pulse 0.\n" +
                        "Si desea volver a intentarlo pulse 1. \n" +
                        "Si quieres ser invitado pulse 2.");

                int cod = controlarExceptionInt();

                switch (cod) {
                    case 0:
                        System.out.println("Saliendo del programa...");
                        return;
                    case 2:
                        menuInvitado();
                        break;
                    case 1:
                        System.out.println("Reintentando LogIn...");
                        break;
                    default:
                        System.out.println("Opción no válida. Reintentando LogIn...");
                        break;
                }
            } else {
                menu(lecturaFicheros.getRol(), username, password);
                break;
            }
        }
    }
     /*
      Menu generico, que nos permite movernos
      entre las diferenctes opciones de los distintos usuarios
     */

    public void menu(String rol, String nombre, String pas) {
        switch (rol) {
            case "Admin":
                menuAdmin();
                break;
            case "Entrenador":
                menuEntrenador(new Entrenador()); //Creamos el entrenador vacio a la espera de saber como recorrer los entrenadores
                break;
            case "Invi":
                login();
                break;
            case "Admin Torneo":
                AdminDAO adminDAO = new AdminDAO(nombre, pas);
                menuAdminTorneo(adminDAO);
                break;
            default:
                System.out.println("Opcion no valida");
        }
    }

    /*
      Metodo para desplegar por consola el menu de admin ( no admin torneo)
     */

    public void menuAdmin() {
        System.out.println("Eres el Admin las opciones son esas :" +
                "\n 0-Salir"+
                "\n 1- Nuevo Torneo");
        int opcion = controlarExceptionInt();
        switch (opcion) {
            case 0:
                menuInvitado();
                break;

            case 1:
                nuevoTorneo();
                break;

            default:
                System.out.println("Error en el programa");
                break;
        }
    }

    public void menuAdminTorneo(AdminDAO admin) {
        if(admin.existeEnDBConCredenciales()){
            System.out.println("¡Acceso concedido! Bienvenido, " + admin.getNombre());
            System.out.println("OPCIONES DE MENU DE ADMIN TORNEO:"+
                    "\n0-SALIR"+
                    "\n1-Exportar Torneo"+
                    "\n2-Combates");
            int i = controlarExceptionInt();
            switch (i) {
                case 0:
                    //menu("Invi","Invi");
                    break;
                case 1:
                    exportarTorneoMain(admin);
                    break;
                case 2:
                    TorneoDTO t = new TorneoDTO();
                    t.mostrarTodosTorneos();
                    System.out.println("Introduce el id del torneo");
                    int a = controlarExceptionInt();
                    admin.mostrarDatosTorneo(a);
                    break;
            }
        }

    }

    public void exportarTorneoMain(AdminDAO admin) {

        System.out.println("Introduce el ID del torneo:");
        int id = scanner.nextInt();

        TorneoDAO torneo = new TorneoDAO();
        torneo.cargarTorneoDB(id);

        torneo.exportarTorneoMain();

    }


     /*
        Metodo para desplegar por consola el menu de entrenador
     */

    public void menuEntrenador(Entrenador e) {

        while (true) {
            System.out.println("Eres el Entrenador las opciones son esas :" +
                    "\n 0- Volver al login" +
                    "\n 1- Ver Carnet" +
                    "\n 2- Exportar Carnet");


            int opcion = controlarExceptionInt();


            if (opcion == 1) {
                System.out.println(e.toString());


            } else if (opcion == 2) {
                e.exportarXML("src/main/Files");


            } else if (opcion == 0) {
                menuInvitado();
                return;
            } else {
                System.out.println("Opcion no valida.Saliendo del programa...");
                break;
            }

        }
    }

    /*
        Metodo para desplegar por consola el menu de invitado
     */
    public void menuInvitado() {
        System.out.println("Eres el Invitado las opciones son esas :" +
                "\n 0-Salir"+
                "\n 1-Nuevo entrenador " +
                "\n 2-Logear" );
        int opcion = controlarExceptionInt();
        switch (opcion) {
            case 0:
                System.out.println("Saliendo del programa...");
                return;
            case 1:
                nuevoEntrenador();
                break;
            case 2:
                login();
                break;
            default:
                System.out.println("Saliendo");
                break;
        }
    }

    /*
      Metodo para crear nuevo entrenador
      Controlamos que el Entrenador no Exista
      Si no existe Comprobar que el code de nacioaldiad Existe
      Si ambos existen creamos, y metemos en credenciales.txt
     */
    public void nuevoEntrenador() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el nombre del entrenador:");
        String nombre = scanner.nextLine();

        pais.mostrarPais();
        System.out.println("Ingrese la nacionalidad del entrenador:");
        String nacionalidad = scanner.nextLine();

        PaisDTO pais = new PaisDTO();
        pais.setNombre(nacionalidad);

        System.out.println("Introduce el ID del torneo al que se quiera introducir:");
        int id = Integer.parseInt(scanner.nextLine());
        TorneoDAO torneo = new TorneoDAO ();
        torneo.setId(id);

        System.out.println("Inserte la pass");
        String pass = scanner.nextLine();

        if(pais.existePaisPorNombre(nacionalidad) && torneo.existeTorneo(id)){
            System.out.println("Todo ok");
            EntrenadorDAO entrenadorDAO = new EntrenadorDAO(nombre, nacionalidad);
            entrenadorDAO.insertarEnDB();
            escribirUltimaLinea(""+nombre+"  "+pass+""+"  Entrenador  "+entrenadorDAO.getId());
        }

    }



    // Comprobamos que no existen credenciales dentro de credenciales.txt


    public void crearCredenciales(String nombre, int id, String tipo) {
        if (lecturaFicheros.comprobarNuevo(nombre)) {
            System.out.println("Usuario repetido");
            nuevoEntrenador();

        } else {
            System.out.println("Inserte contraseña");
            String pass = scanner.nextLine();
            escrituraFicheros.insertarCredenciales(nombre, pass, tipo, id);
        }
    }


    // Método para crear un nuevo administrador para un torneo
// Solicita el nombre y la contraseña del administrador.
// Guarda los datos en la base de datos y los registra en un archivo de texto.
// Devuelve el ID del nuevo administrador creado.
    public int nuevoAdminTorneo() {
        System.out.println("Introduce el Nombre del Admin del Torneo");
        String nombre = scanner.nextLine(); // Solicita el nombre del administrador
        System.out.println("Introduce una contraseña");
        String pass = scanner.nextLine(); // Solicita la contraseña del administrador
        AdminDAO admin = new AdminDAO(nombre, pass); // Crea un objeto AdminDAO con los datos ingresados
        admin.AdminDAONuevoDB(nombre, pass); // Guarda al administrador en la base de datos
        escribirUltimaLinea("" + nombre + "  " + pass + "  " + "Admin Torneo  " + admin.getId()); // Registra los datos en un archivo
        return admin.getId(); // Retorna el ID del nuevo administrador
    }

    // Método para crear un nuevo torneo en la base de datos
// Solicita el nombre del torneo, muestra los países disponibles y pide la región.
// Asocia el torneo al administrador indicado y llama al menú de administración.
    public void nuevoTorneoDB(int idAdmin) {
        System.out.println("Ingrese el nombre del torneo");
        String nombreTorneo = scanner.nextLine(); // Solicita el nombre del torneo
        pais.mostrarPais(); // Muestra los países disponibles
        System.out.println("Ingrese la región del torneo (una letra)");
        char region = scanner.nextLine().charAt(0); // Solicita la región como un carácter
        TorneoDAO torneo = new TorneoDAO(nombreTorneo, region, idAdmin); // Crea un objeto TorneoDAO con los datos
        menuAdmin(); // Llama al menú de administración
    }

    // Método para gestionar la creación de un nuevo torneo
// Permite elegir entre crear un nuevo administrador o usar uno existente.
// Si se elige crear un nuevo admin, se llama a nuevoAdminTorneo y luego a nuevoTorneoDB.
// Si se usa un admin existente, verifica su existencia en la base de datos antes de crear el torneo.
    public void nuevoTorneo() {
        System.out.println("Introduce 1, si vas a crear un nuevo admin, inserta cualquier otro número si vas a usar otro admin");
        int a = Integer.parseInt(scanner.nextLine()); // Solicita la opción al usuario
        int idAdmin;

        switch (a) {
            case 1: // Crear un nuevo administrador
                idAdmin = nuevoAdminTorneo(); // Crea un nuevo administrador y obtiene su ID
                nuevoTorneoDB(idAdmin); // Crea un torneo con el nuevo administrador
                break;
            default: // Usar un administrador existente
                System.out.println("Introduce el ID del admin");
                idAdmin = Integer.parseInt(scanner.nextLine()); // Solicita el ID del administrador
                AdminDAO adminDAO = new AdminDAO(idAdmin); // Crea un objeto AdminDAO con el ID proporcionado
                System.out.println("ID del admin: " + adminDAO.getId());
                if (adminDAO.existeEnDB()) { // Verifica si el administrador existe en la base de datos
                    System.out.println("Sí existe");
                    nuevoTorneoDB(idAdmin); // Crea un torneo con el administrador existente
                } else {
                    System.out.println("No existe"); // Informa que el administrador no existe
                }
        }
    }

    //Esta funcion te lee el .dat

    public void leerTorneos() {
        File f = new File("src/main/Files/Torneos.dat");
        if (f.exists() && f.length() > 0) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
                while (dis.available() > 0) { // Mientras haya datos disponibles en el archivo
                    int id = dis.readInt();                     // Lee el ID del torneo
                    String nombre = dis.readUTF();              // Lee el nombre del torneo
                    char region = dis.readChar();               // Lee la región del torneo
                    String adminNombre = dis.readUTF();         // Lee el nombre del administrador
                    int puntos = dis.readInt();           // Lee la contraseña del administrador

                    // Crear un nuevo objeto Torneo con los datos leídos
                    Torneo torneo = new Torneo(nombre, region, puntos);
                    torneos.add(torneo); // Añadir el torneo a la lista
                }

            } catch (IOException e) {
                System.out.println("Error al leer el archivo de torneos.");
                e.printStackTrace();
            }
        }

    }

    /*Con este metodo controlamos que cuando pides un int por pantalla
     no rompa el programa por no ser int*/

    public int controlarExceptionInt() {
        Scanner scanner = new Scanner(System.in);
        int numero = 0;
        boolean valido = false;

        // Repetir hasta que se ingrese un valor válido
        while (!valido) {
            System.out.print("Por favor, ingresa un número entero: ");
            try {
                // Intentamos parsear el valor ingresado como un entero
                numero = Integer.parseInt(scanner.nextLine());
                valido = true;  // Si no hay error, la entrada es válida
            } catch (NumberFormatException e) {
                System.out.println("¡Error! Debes ingresar un número entero.");
            }
        }
        return numero;
    }

    //Este metodo lee el txt y suma 1 al id de crear nuevoEntrenador

    public int generarIdEntrenador(){
        int contadorId=0;
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/Files/Credenciales.txt"))) {
            String linea;
            while((linea=br.readLine())!=null){
                String[] parts = linea.split("  ");
                if(parts[2].equals("Entrenador")){
                    contadorId++;
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
        }
        return contadorId;
    }


    public void escribirUltimaLinea(String contenido) {
        String archivoPath="src/main/Files/Credenciales.txt";
        File archivo = new File(archivoPath);

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            long length = raf.length() - 1;

            // Mover el puntero a la última línea
            for (long pos = length; pos >= 0; pos--) {
                raf.seek(pos);
                if (raf.readByte() == '\n') {
                    raf.setLength(pos + 1); // Truncar archivo hasta la última línea
                    break;
                }
            }

            // Escribir la nueva línea
            try (FileWriter fw = new FileWriter(archivo, true)) {
                fw.write(contenido + "\n");
                System.out.println("Contenido añadido correctamente a la última línea del archivo.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al escribir en la última línea del archivo.");
        }
    }


}

