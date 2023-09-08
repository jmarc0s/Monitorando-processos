import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ProcessMonitoringTask {

	public static void main(String[] args) {
		int pid;
		int tempoDeMonitoramento;
		String osNome = System.getProperty("os.name");

		if (osNome.startsWith("Windows")) {
			System.out.println(
					"Seu sistema operacional é windows, infelizmente não conseguiremos visualizar o consumo de CPU do seu processo =(");

		} else if (!osNome.startsWith("Linux")) {
			System.out.println(
					"O seu sistema operacional é desconheciso pelo o nosso sistema, algumas coisas podem não funcionar corretamente. Nome do Sistema Operacional:  "
							+ osNome);
		}

		// captura o pid do processo
		while (true) {
			System.out.println("Digite o pid do processo que deseja monitorar");
			Scanner scanner = new Scanner(System.in);

			if (scanner.hasNextInt()) {
				pid = scanner.nextInt();
				break;
			}

			System.out.println("pid invalido!");

		}

		// captura o tempo de monitoramento
		while (true) {
			System.out.println("Digite por quanto tempo (em minutos) deseja monitorar esse processo: ");
			Scanner scanner = new Scanner(System.in);

			if (scanner.hasNextInt()) {
				tempoDeMonitoramento = scanner.nextInt();
				break;
			}

			System.out.println("tempo invalido! Digite apenas o minuto, não os segundos. Exemplo: 15");

		}

		try {
			showInformationProcess(pid, tempoDeMonitoramento);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void showInformationProcess(int pid, int tempoDeMonitoramento) throws IOException {
		final int umMinutoEmMilissegundos = 1000;
		List<Float> listaRegistroMemoria = new ArrayList<>();
		String processInfo = null;

		for (int i = 1; i <= tempoDeMonitoramento; i++) {

			Process process = null;

			try {

				// econtra o processo pelo pid de acordo com o sistema operacional

				if (System.getProperty("os.name").startsWith("Windows")) {
					process = Runtime.getRuntime()
							.exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /FI \"PID eq " + pid + "\"");
				} else {
					process = Runtime.getRuntime().exec("ps -p " + pid + " -o pid,%cpu,%mem,cmd");
				}

				// pega as informações do processo
				Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
				processInfo = scanner.hasNext() ? scanner.next() : "";
				scanner.close();

				if (processInfo.contains(Integer.toString(pid))) {

					System.out.println(processInfo);

					if (System.getProperty("os.name").startsWith("Windows")) {
						writeOnWindows(i, pid, processInfo, listaRegistroMemoria);
					} else {
						writeOnLinux(i, pid, processInfo);
					}

				} else {
					System.out.println("O processo com PID " + pid + " não foi encontrado.");
				}
			} catch (IOException e) {
				System.out.println(
						"Ocorreu algum erro ao tentar capturar ou escrever as informações do processo no arquivo");

			} finally {
				if (process != null) {
					process.destroy();

				}
			}

			// tempo de espera até a proxima medição
			try {
				Thread.sleep(umMinutoEmMilissegundos);

			} catch (InterruptedException e) {
				System.out.println("Algo deu errado!");
			}

		}

		if (System.getProperty("os.name").startsWith("Windows")) {
			writeResultOnWindows(listaRegistroMemoria);
		} else {
			// writeResulteOnLinux
		}

	}

	public static void writeOnWindows(int indexDeEscrita, int pid, String processInforToBeWrite,
			List<Float> listaRegistroMemoria) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("arquivoDeMonitoramento.txt", indexDeEscrita != 1));

		if (indexDeEscrita == 1) {
			writer.write(
					"---------------------------------------------------- Relatorio de monitoramento do processo com o PID: "
							+ pid + " ---------------------------------------------------- \n");
			writer.append("\n" + processInforToBeWrite.split("\\r?\\n")[1] + "\n"
					+ processInforToBeWrite.split("\\r?\\n")[2] + "\n" + processInforToBeWrite.split("\\r?\\n")[3]
					+ " monitoração  de numero: " + indexDeEscrita);

			writer.close();
		} else {
			writer.append(
					"\n" + processInforToBeWrite.split("\\r?\\n")[3] + " monitoração  de numero: " + indexDeEscrita);

			writer.close();
		}

		String[] ultimaStringDividida = processInforToBeWrite.split("\\r?\\n")[3].split("\\s+");

		listaRegistroMemoria.add(Float.valueOf(ultimaStringDividida[ultimaStringDividida.length - 2]));
	}

	public static void writeResultOnWindows(List<Float> listaRegistroMemoria) throws IOException {
		float consumoMedioDeMemoria = (float) listaRegistroMemoria
				.stream()
				.mapToDouble(Float::doubleValue)
				.average()
				.orElse(0.0);

		float consumoMinimoDeMemoria = Collections.min(listaRegistroMemoria);

		float consumoMaximoDeMemoria = Collections.max(listaRegistroMemoria);

		System.out.println("Consumo minimo de memoria: " + consumoMinimoDeMemoria);

		System.out.println("Consumo maximo de memoria: " + consumoMaximoDeMemoria);

		System.out.println("Consumo medio de memoria: " + consumoMedioDeMemoria);

		BufferedWriter writer = new BufferedWriter(new FileWriter("arquivoDeMonitoramento.txt", true));
		writer.append("\n\nConsumo minimo de memoria: " + consumoMinimoDeMemoria);
		writer.append("\nConsumo maximo de memoria: " + consumoMaximoDeMemoria);
		writer.append("\nConsumo medio de memoria: " + consumoMedioDeMemoria);
		writer.close();
	}

	public static void writeOnLinux(int indexDeEscrita, int pid, String processInforToBeWrite) throws IOException {

		// implementar metodo de escrita do linux
		System.out.println("implementar metodo de escrita do linux");
		BufferedWriter writer = new BufferedWriter(new FileWriter("arquivoDeMonitoramento.txt", indexDeEscrita != 1));

	}

}
