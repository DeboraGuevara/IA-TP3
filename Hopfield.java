import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Hopfield {
    private int[][] weightMatrix;
    private int size;

    // Constructor para inicializar la red de Hopfield con el tamaño dado
    public Hopfield(int size) {
        this.size = size;
        this.weightMatrix = new int[size][size];
    }

    // Método para entrenar la red de Hopfield con un patrón
    public void train(int[] pattern) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    weightMatrix[i][j] += pattern[i] * pattern[j];
                }
            }
        }
    }

    // Método para recordar un patrón dado 
    public int[] recall(int[] pattern) {
        int[] output = Arrays.copyOf(pattern, pattern.length);
        boolean stable = false;

       //estabilizamos
        while (!stable) {
            stable = true;
            for (int i = 0; i < size; i++) {
                int sum = 0;
                for (int j = 0; j < size; j++) {
                    sum += weightMatrix[i][j] * output[j];
                }
                int newState = sum >= 0 ? 1 : -1;
                if (newState != output[i]) {
                    stable = false;
                    output[i] = newState;
                }
            }
        }

        return output;
    }

    // leemos y convertimos imagen en una matriz binaria
    public static int[] readImage(String filename, int width, int height) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("El archivo " + filename + " no existe.");
        }
        
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("No se pudo leer la imagen " + filename + ".");
        }
        
        int[] binaryImage = new int[width * height];
        int index = 0;

        // Convertir cada píxel de la imagen a binario usando umbral
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int gray = (pixel >> 16) & 0xff; // Canal rojo como escala de grises
                binaryImage[index++] = gray > 128 ? 1 : -1; // Umbral para convertir a binario
            }
        }

        return binaryImage;
    }

    public static void main(String[] args) {
        int width = 10;  
        int height = 10; 
        int size = width * height; 
        Hopfield hopfield = new Hopfield(size);

        try {
            // Leer el patrón de entrenamiento desde la imagen
            int[] pattern = readImage("/Users/deboraguevara/eclipse-workspace/TP3/src/arochico.jpg", width, height);
            hopfield.train(pattern);

            // Leer el patrón de entrada ruidoso desde la imagen 
            int[] inputPattern = readImage("/Users/deboraguevara/eclipse-workspace/TP3/src/aro.jpg", width, height);
            int[] outputPattern = hopfield.recall(inputPattern);
              

            // Imprimir el patrón de salida
            for (int i = 0; i < size; i++) {
                if (i % width == 0) System.out.println();
                System.out.print((outputPattern[i] == 1 ? "x" : " ") + " ");
         
                //codigo
                
            }
            System.out.println(); 

            // Calcular el centro del círculo en el patrón de salida
            int centerX = 0, centerY = 0, count = 0;
            for (int i = 0; i < size; i++) {
                if (outputPattern[i] == 1) { // Si el píxel pertenece al círculo
                    int x = i % width;
                    int y = i / width;
                    centerX += x;
                    centerY += y;
                    count++;
                }
            }
            centerX /= count;
            centerY /= count;

            System.out.println("Centro del círculo en el patrón de salida:");
            System.out.println("X: " + centerX);
            System.out.println("Y: " + centerY);

        } catch (IOException e) {
            System.err.println("Error al leer la imagen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
