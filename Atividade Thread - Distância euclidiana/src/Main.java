import java.io.*;
import java.util.*;

public class Main extends Thread {

	private List<String[]> lista = new ArrayList<String[]>();
	
	private double menor = 0;
	private int maisProximo = 0;
	private double duracao = 0;
	private int inicio = 0;
	private int fim = 0;

	public Main(List<String[]> lista, int inicio, int fim) {
		
		this.lista = lista;
		this.inicio = inicio;
		this.fim = fim;
	}
	
	public double getMenor() {
		return menor;
	}
	
	public int getMaisProximo() {
		return maisProximo;
	}
	
	public double getDuracao() {
		return duracao;
	}

	public static void printArray(String[] vector) {
		for (int n = 0; n < vector.length; n++) {
			System.out.print(vector[n] + " ");
		}
		System.out.println();

	}

	public static List<String[]> extrairDadosDoCSV() throws Exception {
		
		List<String[]> lista = new ArrayList<String[]>();
		File f = new File("D:\\dadosSD.csv");

		// Verifica se o arquivo � encontrado no ambiente
		if (f.exists()) {
			try (Scanner fileReader = new Scanner(f)) {
				while (fileReader.hasNext()) {
					lista.add(fileReader.nextLine().trim().split(","));
				}
			}
			
			return lista;
		} else {
			
			System.err.println(" O arquivo n�o encontrado!");
			return null;
		}
	}

	public void run () {
		
		try {
			// O tempo incial de execu��o do Thread
			double tempoInicio = System.currentTimeMillis();
			double soma = 0;
			double distancia = 0;

			menor = Double.MAX_VALUE;
			maisProximo = 0;
			for (int m = inicio; m < fim; m++) {
				soma = 0;
				for (int n = 0; n < lista.get(m).length; n++) {
					double p = Double.parseDouble(lista.get(0)[n]); // Primeira linha (target)
					double q = Double.parseDouble(lista.get(m)[n]); // Linhas demais

					soma += Math.pow((p - q), 2);
				}

				// C�lculo de dist�ncia
				distancia = Math.sqrt(soma);

				if (distancia < menor) {
					menor = distancia;
					maisProximo = m;
				}

			}
			
			
			// O tempo final de execu��o do Thread
			double tempoFinal = System.currentTimeMillis();
			
			// Dura��o de execu��o do Thread
			duracao = (tempoFinal - tempoInicio);
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		List<String[]> lista = new ArrayList<String[]>();
		
		// Lista obtendo o m�todo para extrair dados CSV no arquivo
		lista = extrairDadosDoCSV();
	
		// Lista de threads
		List<Main> threads = new ArrayList<Main>();
		
		int quantidadeThread = 4;
		
		/*
		 * O tamanho sublista divide o tamanho da lista de dados por quantidade de Threads 
		 */
		int tamSubLista = lista.size()/quantidadeThread;

		System.out.println(String.format("\nTamanho Lista: %d - Quantidade de threads: %d - Tamanho Sublista: %d \n", lista.size(), quantidadeThread, tamSubLista));
		for (int i = 1; i < lista.size() - 1; i += tamSubLista) {
			
			System.out.println(String.format("In�cio: %04d - Fim: %d", i, i + tamSubLista >= lista.size() ? lista.size()  : i + tamSubLista));
			Main novaThread = new Main(lista, i, i + tamSubLista >= lista.size() ? lista.size()  : i + tamSubLista);
			
			/* Adicionando cada uma nova thread quando inicia a sua execu��o */
			
			novaThread.start();
			threads.add(novaThread);
		}
	   	
		// Considera que a thread 0 obteve a menor distancia 
		Main threadMenorDistancia = threads.get(0);
		
		// Aguarda primeira thread encerrar
		threadMenorDistancia.join();
		// Pecorre da segunda thread at� a �ltima
		for (int i = 1; i < threads.size(); ++i) {
			
			// Aguarda a conclus�o da thread
			threads.get(i).join();
			
			// Se a dist�ncia euclidiana obtida nesta parte for menor que a menor at� ent�o obtida por cada uma thread
			if (threads.get(i).getMenor() < threadMenorDistancia.getMenor()) {
				
				// Esta parte passa a ser a thread com a menor dist�ncia
				threadMenorDistancia = threads.get(i);
			}
		}
		
		System.out.println(String.format("\nMenor valor de dist�ncia: %.2f - Dura��o: %.3f ms", threadMenorDistancia.getMenor(), threadMenorDistancia.getDuracao()));
		
		// Posi��o localizando a lista de valores que h� valor menor de distancia
		System.out.println(String.format("Item mais pr�ximo (%d): ", threadMenorDistancia.getMaisProximo())); 
		
		printArray((lista.get(threadMenorDistancia.getMaisProximo())));

	}

}
