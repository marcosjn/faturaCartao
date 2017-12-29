package br.com.marcosjn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Scanner;

public class FaturaCartao {

	public static void main(String[] args) {
		Float dividaProximaFatura = 0f;
		Float dividaTotal = 0f;
		Float dolar = 3.5f;
		String arquivo = "faturaCartao";
		String extensao = ".txt";

		Boolean argDolar = false;
		Boolean argFile = false;
		for (String arg : args) {
			if (argDolar) {
				argDolar = false;
				try {
					dolar = Float.valueOf(arg);
				} catch (NumberFormatException nfex) {
					System.out.println("O parametro deve ser um numero correspondente ao valor do dolar.");
					System.exit(0);
				}
			} else if (argFile) {
				argFile = false;
				String[] arquivoCompleto = arg.split("\\.");
				if (arquivoCompleto.length != 2) {
					System.out.println("Formato de arquivo invalido.");
					System.exit(0);
				} else {
					arquivo = arquivoCompleto[0];
					extensao = "." + arquivoCompleto[1];
				}
			}
			if (arg.equals("-d")) {
				argDolar = true;
			} else if (arg.equals("-f")) {
				argFile = true;
			}
		}

		try {
			Scanner scanner = new Scanner(new FileReader(arquivo + extensao)).useDelimiter("\\n");

			FileWriter arq = new FileWriter(arquivo + ".csv");
			PrintWriter gravarArq = new PrintWriter(arq);
			while (scanner.hasNext()) {
				String linha = scanner.next();
				if (linha.length() >= 12) {
					// Imprime as linhas que constam o nome do usuario do cartao
					if (linha.substring(9, 12).matches("[1-9]-[A-Z]")) {
						System.out.println("Nome: " + linha);
					}
					// Separa somente as linhas que começam por uma data no
					// formato DD/MM
					if (linha.substring(0, 6).matches("[0-3][0-9]/[0-1][0-9] ")
							|| linha.substring(0, 10).matches("[0-3][0-9]\\.[0-1][0-9]\\.[2][0][1-3][0-9]")) {
						// Verifica se tem parcelamento
						if (linha.substring(24, 34).matches("PARC [0-3][0-9]/[0-1][0-9]")) {
							Integer parcela = Integer.valueOf(linha.substring(29, 31));
							Integer total = Integer.valueOf(linha.substring(32, 34));
							Float valor = Float.valueOf(linha.substring(61, 70).trim().replace(',', '.'));
							if (valor == 0f) {
								valor = Float.valueOf(linha.substring(73, 82).trim().replace(',', '.')) * dolar;
							}
							if (total > parcela) {
								dividaProximaFatura += valor;
								dividaTotal += valor * (total - parcela);
							}
						} else {
							linha = linha.substring(0, 33) + " " + linha.substring(33);
						}
						String csv = linha.substring(0, 10) + ";" + linha.substring(10, 34).trim() + ";"
								+ linha.substring(61, 71).trim() + ";" + linha.substring(73, 82).trim() + "\n";
						System.out.print(csv);
						gravarArq.printf(csv);
					}
				}
			}
			String divida1 = "88/88;DIVIDA PROXIMA FATURA;"
					+ new DecimalFormat("#.##").format(dividaProximaFatura).toString() + "\n";
			System.out.print(divida1);
			gravarArq.printf(divida1);
			String divida2 = "99/99;DIVIDA TOTAL;" + new DecimalFormat("#.##").format(dividaTotal).toString() + "\n";
			System.out.print(divida2);
			gravarArq.printf(divida2);
			arq.close();
			System.out.println("----------------------------------------------");
			System.out.println("Criado arquivo " + arquivo + ".csv");
		} catch (FileNotFoundException fex) {
			System.out.println("Arquivo " + arquivo + extensao + " não encontrado!");
		} catch (IOException ioex) {
			System.out.println("Erro ao gravar arquivo");
			ioex.printStackTrace();
		}

	}

}
