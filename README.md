# won-mailbot
MailBot description tbd

Extracted from won-bot README.md
## Mail2Won Bot

> **NOTE:** The `Mail2WonBotApp` may be currently not entirely functional due to code structure changes in the main WoN applications.

[Mail2WonBotApp](src/main/java/won/bot/app/Mail2WonBotApp.java) can be used to create atoms retrieved from a given email address. This bot acts like the owner application as it allows users to create atoms, open/close connections and communicate with others.

To run the [Mail2WonBotApp](src/main/java/won/bot/app/Mail2WonBotApp.java), an argument specifying the configuration location is needed, e.g:

-DWON_CONFIG_DIR=C:/webofneeds/conf.local

Furthermore you need to set the following properties within mail-bot.properties to ensure a connection to an incoming and outgoing mail-server:

```
mailbot.email.address=emailadress
mailbot.email.user=username
mailbot.email.password=pass
mailbot.email.imap.host=imap.gmail.com
mailbot.email.imap.port=993
mailbot.email.smtp.host=smtp.gmail.com
mailbot.email.smtp.port=587
```

Make sure the config folder contains the relevant property files, and you have specified the values of the properties relevant for the system being tested, i.e.:

- in [node-uri-source.properties](../conf/node-uri-source.properties)
  - won.node.uris - specify values of nodes being tested - the bot will react to atoms published on those nodes
- in [owner.properties](../conf/owner.properties)
  - specify default node data (node.default.host/scheme/port) - the bot will create its own atoms on that node
  - make sure both a path to keystore and truststore (keystore/truststore.location) and their password (keystore/truststore.password) is specified.  For additional details on the necessary keys and certificates, refer to the Web of Needs [installation notes](https://github.com/researchstudio-sat/webofneeds/blob/master/documentation/installation-cryptographic-keys-and-certificates.md).

### Usage

> **NOTE:** Due to atom structure changes, the format described here may be accepted by the `Mail2WonBot`, but not by connected node or other clients.

You can send an email with a subject starting with either [WANT], [OFFER], [TOGETHER], [CRITIQUE] to the configured mailadress, to create an atom of the given type. The content of the email will be used as the description, while the subject line will be used as title. Furthermore, tags (strings starting with #) will be extracted from the email and will be stored within the created atom.

You will then receive emails when the matcher finds connections to this created atom, you can answer those e-mails via the reply function of your email-client, for now we support the following commands (which will be retrieved from the replymessage-body):

A reply that starts with:

- "close" or "deny" will close the respective connection
- "connect" sends a request to the other atom or opens the connection if the status is already request received
- answering a mail with anything that does not contain any of the keywords above will automatically open a connection and send the replymessage-body as a textmessage

A reply for an already open connection (`connectionState: Connected`) will send the replymessage-body as a textmessage.

Every remote message sent to this connection will be sent to you as an e-mail as well.
