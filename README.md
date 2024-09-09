[日本語](README_ja.md)

# Batch Translator

Batch Translator is a CLI tool that translates text files.
The primary target audience is OSS development project members who need to translate the README and documentation.
The primary usage is as follows:

- Single file translation (for example, README. md)
- Batch translation of files under a directory with a specific extension (for example, documents using [AsciiDoctor](https://AsciiDoctor.org/))

## For Users

### Execution Environment

The following software is required to run the Batch Translator:

- Java 11+
- Maven 3.6 + (when used as the Maven Plugin)

#### Usage

The Batch Translator can be run as a Java command or as a Maven plugin.

1.  Create API Key (#Create API Key)
1.  Run as Java Command or Run as Maven Plugin

#### Creating an API Key

Batch Translator uses "Minna no Automaton Translation @ TexTra ®", "Amazon Translate" and "Cloud Translation API" for the translation engine. To use the translation function, please create an account at one of the following sites.

##### Minna no Jidousha Hon' yaku @ TexTra ®

https://mt-auto-minhon-mlt.ucri.jgn-x.jp/

After creating the account, save the user ID, API KEY, and API SECRET from the Settings page (https://mt-auto-minhon-mlt.ucri.jgn-x.jp/content/setting/user/edit/) to the batch-translator. properties file.

The batch-translator. properties file must be stored in a directory named. aulait under the user home directory.

- Windows

```bat
mkdir %USERPROFILE%\.aulait
notepad %USERPROFILE%\.aulait\batch-translator.properties
```

- macOs

```sh
mkdir ~/.aulait
nano ~/.aulait/batch-translator.properties
```

- batch-translator.properties

```properties
api_key=your_api_key
api_secret=your_api_secret
name=your_user_name
```

##### When to Use Amazon Translate

https://portal.aws.amazon.com/billing/signup#/start

After creating the account, refer to the User Guide (https://docs.aws.amazon.com/en/cli/latest/userguide/cli-configure-files.html) and save the aws _ access _ key _ id and aws _ secret _ access _ key in the credentials file.

Create the credentials file in your home directory under the. aws folder.

Ex. ~/.aws/credentials
```properties
[default]
aws_access_key_id=AKIAIOSFODNN7EXAMPLE
aws_secret_access_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

##### When to Use the Cloud Translation API

https://console.cloud.google.com/getting-started

After you create your account, use Enable API (https://cloud.google.com/translate/docs/setup#api) to enable the Cloud Translation API.

Then configure the ADC with the help of Configure ADC with a Google Account (https://cloud.google.com/docs/authentication/provide-credentials-adc#google-idp).

Pricing for the Cloud Translation API is available [here] at https://cloud.google.com/translate/pricing?hl=ja#basic-pricing.

#### Run with Java Command

```
curl -o batch-translator-core-1.0.0.jar https://repo1.maven.org/maven2/dev/aulait/bt/batch-translator-core/1.0.0/batch-translator-core-1.0.0.jar

java -jar batch-translator-core-1.0.0.jar -m Mode -s Source -t Target -e Engine(minhon or aws)
```

Example 1) Command that translates README _ ja. md from Japanese to English and outputs it to README. md

```
java -jar batch-translator-core-1.0.0.jar -m ja2en -s README_ja.md -t README.md -e aws
```

Example 2) A command that translates all files with the extension adoc under the docs directory from Japanese to English and outputs them to the docs/en directory

```
java -jar batch-translator-core-1.0.0.jar -m ja2en -p *.adoc -s docs -t docs/en -e aws
```

#### Run as Maven Plugin

Add the Batch Tracer Maven Plugin to pom. xml.

- pom.xml

```xml
<bulid>
  <plugins>
    <plugin>
      <groupId>dev.aulait.bt</groupId>
      <artifactId>batch-translator-maven-plugin</artifactId>
      <version>1.0.0</version>
    </plugin>
  </plugins>
</build>
```

```
mvn batch-translator:translate -Dbt.source=README_ja.md -Dbt.target=README.md -Dbt.mode=ja2en -Dbt.filePattern=*.adoc -Dbt.engine=aws
```

### Bug Reports, Feature Requests

If you have a bug or feature request related to Batch Translator, please submit an issue to [Issues] on this GitHub project (https://GitHub.com/project-au-lait/batch-translator/issues).

## License

The Batch Translator is available at Apache Lisence 2.0.