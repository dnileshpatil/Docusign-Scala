package com.docusign

/**
  * Created by dnilesh on 3/10/17.
  *
  */

import com.docusign.esign.api._
import com.docusign.esign.client._
import com.docusign.esign.model._
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException
import java.util.Base64


object SignitureRequestScala {
  
  def main(args: Array[String]) : Unit = {

    // Enter your DocuSign credentials:
    val UserName = "nilesh.p57@gmail.com"
    val Password = "Unlockme@this"
    val IntegratorKey = "72aa8066-e918-42e4-9930-11dafa94b180"

    // for production environment update to "www.docusign.net/restapi"
    val BaseUrl = "https://demo.docusign.net/restapi"

    // initialize the api client for the desired environment
 var apiClient:ApiClient = new ApiClient()
    apiClient.setBasePath(BaseUrl)

    // create JSON formatted auth header
    val creds = "{\"Username\":\"" + UserName + "\",\"Password\":\"" + Password + "\",\"IntegratorKey\":\"" + IntegratorKey + "\"}"
    apiClient.addDefaultHeader("X-DocuSign-Authentication", creds)

    // assign api client to the Configuration object
    Configuration.setDefaultApiClient(apiClient)

    // create an empty list that we will populate with account(s)
    var loginAccounts:java.util.List[LoginAccount] = null

    try {

      // login call available off the AuthenticationApi
      lazy val authApi = new AuthenticationApi

      // login has some optional parameters we can set
      //val loginOps = new com.docusign.esign.api.AuthenticationApi()
     // loginOps.setApiPassword("true")
      //loginOps.set("true")
      val loginInfo = authApi.login()

      // note that a given user may be a member of multiple accounts
      loginAccounts = loginInfo.getLoginAccounts
      System.out.println("LoginInformation: " + loginAccounts)

    } catch {
      case ex: ApiException =>
        System.out.println("Exception: " + ex)
    }

    // specify a document we want signed
    val SignTest1File = "/Please_Sign_my_Java_SDK_Envelope_(Embedded_Si.pdf"

    // create a byte array that will hold our document bytes
    var fileBytes:Array[Byte] = null

    try {
      val currentDir = System.getProperty("user.dir")

      // read file from a local directory
      val path = Paths.get(currentDir + SignTest1File)
      fileBytes = Files.readAllBytes(path)
    } catch {
      case ioExcp: IOException =>

        // handle error
        System.out.println("Exception: " + ioExcp)
        return
    }

    // create an envelope that will store the document(s), tab(s), and recipient(s)
    val envDef = new EnvelopeDefinition
    envDef.setEmailSubject("[Java SDK] - Please sign this doc")

    // add a document to the envelope
    val doc = new Document
    val base64Doc = Base64.getEncoder.encodeToString(fileBytes)
    doc.setDocumentBase64(base64Doc)
    doc.setName("TestFile.pdf") // can be different from actual file name

    doc.setDocumentId("1")

    // create a list of docs and add it to the envelope
    val docs = new java.util.ArrayList[Document]
    docs.add(doc)
    envDef.setDocuments(docs)

    // add a recipient to sign the document, identified by name, email, and recipientId
    val signer = new Signer
    signer.setEmail("dnileshpatil@gmail.com")
    signer.setName("Nilesh_Signer")
    signer.setRecipientId("1")

    // create a signHere tab somewhere on the document for the signer to sign
    // default unit of measurement is pixels, can be mms, cms, inches also
    val signHere = new SignHere
    signHere.setDocumentId("1")
    signHere.setPageNumber("1")
    signHere.setRecipientId("1")
    signHere.setXPosition("100")
    signHere.setYPosition("150")

    // can have multiple tabs, so need to add to envelope as a single element list
    val signHereTabs = new java.util.ArrayList[SignHere]
    signHereTabs.add(signHere)
    val tabs = new Tabs
    tabs.setSignHereTabs(signHereTabs)
    signer.setTabs(tabs)

    // add recipients (in this case a single signer) to the envelope
    envDef.setRecipients(new Recipients)
    envDef.getRecipients.setSigners(new java.util.ArrayList[Signer])
    envDef.getRecipients.getSigners.add(signer)

    // send the envelope by setting |status| to "sent". To save as a draft set to "created"
    envDef.setStatus("sent")
    try {

      // use the |accountId| we retrieved through the Login API to create the Envelope
      val accountId = loginAccounts.get(0).getAccountId

      // instantiate a new EnvelopesApi object
      val envelopesApi = new EnvelopesApi

      // call the createEnvelope() API to send the signature request!
      val envelopeSummary = envelopesApi.createEnvelope(accountId, envDef)
      System.out.println("EnvelopeSummary: " + envelopeSummary)
    } catch {
      case ex: ApiException =>
        System.out.println("Exception: " + ex)
    }
    // end main()}
    //
  }
}
