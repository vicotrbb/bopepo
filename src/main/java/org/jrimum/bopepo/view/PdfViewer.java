/*
 * Copyright 2008 JRimum Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * Created at: 30/03/2008 - 18:05:16
 * 
 * ================================================================================
 * 
 * Direitos autorais 2008 JRimum Project
 * 
 * Licenciado sob a Licença Apache, Versão 2.0 ("LICENÇA"); você não pode usar
 * esse arquivo exceto em conformidade com a esta LICENÇA. Você pode obter uma
 * cópia desta LICENÇA em http://www.apache.org/licenses/LICENSE-2.0 A menos que
 * haja exigência legal ou acordo por escrito, a distribuição de software sob
 * esta LICENÇA se dará “COMO ESTÁ”, SEM GARANTIAS OU CONDIÇÕES DE QUALQUER
 * TIPO, sejam expressas ou tácitas. Veja a LICENÇA para a redação específica a
 * reger permissões e limitações sob esta LICENÇA.
 * 
 * Criado em: 30/03/2008 - 18:05:16
 * 
 */

package org.jrimum.bopepo.view;
import static org.jrimum.utilix.Objects.isNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.pdf.Files;
import org.jrimum.bopepo.pdf.PdfDocMix;
import org.jrimum.utilix.Exceptions;

/**
 * <p>
 * Classe utilizada para preencher o PDF do boleto com os dados do título e boleto.
 * </p>
 * 
 * @author <a href="http://gilmatryx.googlepages.com/">Gilmar P.S.L.</a>
 * @author <a href="mailto:misaelbarreto@gmail.com">Misael Barreto</a>
 * @author <a href="mailto:romulomail@gmail.com">Rômulo Augusto</a>
 * 
 * @since 0.2
 * 
 * @version 0.2
 */
class PdfViewer {

	private static Logger log = Logger.getLogger(PdfViewer.class);

	private Boleto boleto;

	private byte[] template;
	
	private ResourceBundle resourceBundle;
	
	private PdfDocMix doc;

	/**
	 * Para uso interno do componente
	 * 
	 * @since 0.2
	 * 
	 */
	protected PdfViewer() {
		
		resourceBundle = new ResourceBundle();
	}
	
	/**
	 * Para uso interno do componente
	 * 
	 * @param boleto
	 * 
	 * @since 0.2
	 * 
	 */
	protected PdfViewer(Boleto boleto) {

		resourceBundle = new ResourceBundle();

		this.boleto = boleto;
	}
	
	/**
	 * Para uso interno do componente
	 * 
	 * @param boleto Boleto para visualização
	 * @param template Template a ser utilizado na visualização
	 * 
	 * @since 0.2
	 * 
	 */
	protected PdfViewer(Boleto boleto, byte[] template) {
		
		resourceBundle = new ResourceBundle();
		
		this.boleto = boleto;
		
		setTemplate(template);
	}
	
	/**
	 * Retorna o boleto em forma de arquivo PDF.
	 * 
	 * @param destPath
	 *            Caminho completo do arquivo o qual o boleto será gerado
	 * @return Boleto em forma de arquivo PDF
	 * 
	 * @since 0.2
	 * 
	 */
	protected File getFile(String destPath) {
		
	
		return getFile(new File(destPath));
	}
	
	/**
	 * Retorna o boleto em forma de arquivo PDF.
	 * 
	 * @param destPath
	 *            Arquivo o qual o boleto será gerado
	 * @return Boleto em forma de arquivo PDF
	 * @throws IllegalStateException
	 *             Caso ocorral algum problema imprevisto
	 *             
	 * @since 0.2
	 * 
	 */
	protected File getFile(File destFile) {
		
		try {

			processarPdf();
			
			return doc.toFile(destFile);
			
		} catch (Exception e) {
			
			log.error("Erro durante a criação do arquivo! " + e.getLocalizedMessage(), e);
			
			return Exceptions.throwIllegalStateException("Erro ao tentar criar arquivo! " +"Causado por " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Retorna o arquivo PDF em um stream de array de bytes.
	 * 
	 * @return O PDF em stream
	 * 
	 * @since 0.2
	 * 
	 */
	protected ByteArrayOutputStream getStream() {
		
		try {

			processarPdf();
			
			return doc.toStream();
			
		} catch (Exception e) {
			
			log.error("Erro durante a criação do stream! " + e.getLocalizedMessage(), e);
			
			return Exceptions.throwIllegalStateException("Erro durante a criação do stream! " +"Causado por " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Retorna o arquivo PDF em array de bytes.
	 * 
	 * @return O PDF em array de bytes
	 * 
	 * @since 0.2
	 * 
	 */
	protected byte[] getBytes() {
		
		try {

			processarPdf();
			
			return doc.toBytes();
			
		} catch (Exception e) {
			
			log.error("Erro durante a criação do array de bytes! " + e.getLocalizedMessage(), e);
			
			return Exceptions.throwIllegalStateException("Erro durante a criação do array de bytes! " +"Causado por " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Retorna o template atual do viewer em array de bytes.
	 * 
	 * @return Template em bytes
	 * 
	 * @since 0.2
	 * 
	 */
	protected byte[] getTemplate() {
		return template;
	}
	
	/**
	 * Define o template que será utilizado para construir o boleto.
	 * 
	 * @param template
	 * 
	 * @since 0.2
	 * 
	 */
	protected void setTemplate(byte[] template) {
		this.template = template;
	}
	
	/**
	 * Define o template que será utilizado para construir o boleto.
	 * 
	 * @param templateUrl
	 * 
	 * @since 0.2
	 * 
	 */
	protected void setTemplate(URL templateUrl) {
		try {
			setTemplate(templateUrl.openStream());
		} catch (IOException e) {
			Exceptions.throwIllegalStateException(e);
		}
	}

	/**
	 * Define o template que será utilizado para construir o boleto.
	 * 
	 * @param templateInput
	 * 
	 * @since 0.2
	 * 
	 */
	protected void setTemplate(InputStream templateInput) {
		try {
			setTemplate(Files.toByteArray(templateInput));
		} catch (IOException e) {
			Exceptions.throwIllegalStateException(e);
		}
	}

	/**
	 * Define o template que será utilizado para construir o boleto.
	 * 
	 * @param templatePath
	 * 
	 * @since 0.2
	 * 
	 */
	protected void setTemplate(String templatePath) {
		setTemplate(new File(templatePath));
	}

	/**
	 * Define o template que será utilizado para construir o boleto.
	 * 
	 * @param templateFile
	 * 
	 * @since 0.2
	 * 
	 */
	protected void setTemplate(File templateFile) {
		try {
			setTemplate(Files.fileToBytes(templateFile));
		} catch (IOException e) {
			Exceptions.throwIllegalStateException(e);
		}
	}
	
	/**
	 * Habilita o modo full compression do PDF veja
	 * {@link com.lowagie.text.pdf.PdfStamper#setFullCompression()}.
	 * 
	 * <p>
	 * Itext doc: <i>Sets the document's compression to the new 1.5 mode with
	 * object streams and xref streams.</i>
	 * </p>
	 * 
	 * @param option
	 *            Escolha de compressão.
	 *            
	 * @since 0.2
	 *        
	 */
	protected void setFullCompression(boolean option){
		doc.withFullCompression(option);
	}
	
	/**
	 * @return the boleto
	 * 
	 * @since 0.2
	 * 
	 */
	protected Boleto getBoleto() {
		return this.boleto;
	}
	
	/**
	 * Executa os seguintes métodos na sequência:
	 * <ol>
	 * <li>{@linkplain #inicializar()}</li>
	 * <li>{@linkplain #preencher()}</li>
	 * <li>{@linkplain #finalizar()}</li>
	 * </ol>
	 * 
	 * @since 0.2
	 */
	private void processarPdf(){
		
		if (isTemplateFromResource()) {
			doc = PdfDocMix.createWithTemplate(getTemplateFromResource());
			
		} else {
			doc = PdfDocMix.createWithTemplate(getTemplate());
		}
		
		BoletoDataBuilder boletoData =  new BoletoDataBuilder(resourceBundle, boleto);
		
		doc.putAllTexts(boletoData.texts());
		doc.putAllImages(boletoData.images());
	}

	/**
	 * Retorna o template padrão a ser usado, dependendo se o boleto é com ou
	 * sem sacador avalsita.
	 * 
	 * @return URL do template padrão
	 * 
	 * @since 0.2
	 * 
	 */
	private byte[] getTemplateFromResource() {
		
		if (boleto.getTitulo().hasSacadorAvalista()) {
			
			return resourceBundle.getTemplateComSacadorAvalista();
			
		} else {
			
			return resourceBundle.getTemplateSemSacadorAvalista();
		}
	}

	/**
	 * Verifica se o template que será utilizado virá do resource ou é externo,
	 * ou seja, se o usuário definiu ou não um template.
	 * 
	 * @return true caso o template que pode ser definido pelo usuário for null;
	 *         false caso o usuário tenha definido um template.
	 * 
	 * @since 0.2
	 * 
	 */
	private boolean isTemplateFromResource() {
		
		return isNull(getTemplate());
	}


	/**
	 * Exibe os valores de instância.
	 * 
	 * @see org.jrimum.utilix.Objects#toString()
	 */
	@Override
	public String toString() {

		ToStringBuilder tsb = new ToStringBuilder(this);

		tsb.append(boleto);

		return tsb.toString();
	}
}
