package com.example.lukas.spezl.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.example.lukas.spezl.R;

public class AGBActivity extends AppCompatActivity {

    private String privacyPolicy, termsOfUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agb);

        readStrings();

        TextView privacyPolicyView = (TextView) findViewById(R.id.privacyPolicy);
        TextView termsOfUseView = (TextView) findViewById(R.id.termsOfUse);

        privacyPolicyView.setText(privacyPolicy);
        termsOfUseView.setText(termsOfUse);

        setResult(RESULT_OK);
    }

    public void readStrings() {
        privacyPolicy = "Datenschutzerklärung App-Spezl\n" +
                "\n" +
                "\n" +
                "Der Schutz und die Sicherheit von persönlichen Daten hat bei uns eine hohe Priorität. Daher halten wir uns strikt an die Regeln des deutschen Bundesdatenschutzgesetzes (BDSG). Nachfolgend werden Sie darüber informiert, welche Art von Daten erfasst und zu welchem Zweck sie erhoben werden.\n" +
                "\n" +
                "1. Datenübermittlung/Datenprotokollierung\n" +
                "\n" +
                "Beim Besuch dieser Seite verzeichnet der Web-Server automatisch Log-Files, die keiner bestimmten Person zugeordnet werden können. Diese Daten beinhalten z.B.\n" +
                "\n" +
                "-\tDen Browsertyp und - version\n" +
                "-\tWebsite, von der aus Sie uns besuchen (Referrer URL)\n" +
                "-\tDatum und Uhrzeit ihres Zugriffes\n" +
                "-\tIhre Internet Protokoll (IP)-Adresse\n" +
                "\n" +
                "Diese Daten werden nur zum Zweck der statistischen Auswertung gesammelt. Eine Weitergabe an, zu kommerziellen und nicht kommerziellen Zwecken, findet nicht statt.\n" +
                "\n" +
                "2. Nutzung persönlicher Daten\n" +
                "\n" +
                "Persönliche Daten werden nur erhoben oder verarbeitet, wenn Sie diese Angaben freiwillig, z.B. im Rahmen einer Registrierung oder Erstellung eines Events. Sofern keine erforderlichen Gründe im Zusammenhang einer Geschäftsabwicklung bestehen, können Sie jederzeit die zuvor erteilte Genehmigung Ihrer Persönlichen Datenspeicherung mit sofortiger Wirkung schriftlich (E-Mail oder postalisch) widerrufen. Ihre Daten werden nicht an Dritte weitergegeben, es sei denn, eine Weitergabe ist aufgrund gesetzlicher Vorschriften erforderlich.\n" +
                "\n" +
                "3. Auskunft, Änderung und Löschung ihrer Daten\n" +
                "\n" +
                "Sie können aufgrund des Bundesdatenschutzgesetzes jederzeit bei uns schriftlich nachfragen, ob und welche personenbezogene Daten bei uns über Sie gespeichert sind. Eine entsprechende Mitteilung hierzu erhalten Sie umgehend.\n" +
                "\n" +
                "4. Sicherheit ihrer Daten\n" +
                "\n" +
                "Ihre uns zu Verfügung gestellten persönlichen Daten werden durch Ergreifung aller technischen und organisatorischen Maßnahmen basierend auf den 8. Grundsätzen im BDSG, so gesichert, dass sie für den Zugriff unberechtigter Dritter unzugänglich sind.\n" +
                "\n" +
                "5.Änderungen dieser Datenschutzbestimmungen\n" +
                " \n" +
                "Wir werden diese Richtlinien zum Schutz Ihrer persönlichen Daten von Zeit zu Zeit aktualisieren. Sie sollten sich diese Richtlinien gelegentlich ansehen, um auf dem Laufenden darüber zu bleiben, wie wir Ihre Daten schützen und die Inhalte unserer Website stetig verbessern. Sollten wir wesentliche Änderungen bei der Sammlung, der Nutzung und/oder der Weitergabe der uns von Ihnen zur Verfügung gestellten personenbezogenen Daten vornehmen, werden wir Sie durch einen eindeutigen und gut sichtbaren Hinweis auf der Website darauf aufmerksam machen. Mit der Nutzung der Webseite erklären Sie sich mit den Bedingungen dieser Richtlinien zum Schutz persönlicher Daten einverstanden.Bei Fragen zu diesen Datenschutzbestimmungen wenden Sie sich bitte über unsere Kontakt-Seite an uns.\n" +
                "\n";
        termsOfUse = "Nutzungsbedingungen Spezl\n" +
                "\n" +
                "Willkommen bei Spezl!\n" +
                " \n" +
                "Vielen Dank, dass Du dich für Spezl entschieden hast. Wenn Du die App nutzt, bist Du verpflichtet dich an unsere Nutzungsbedingungen zu halten.\n" +
                "\n" +
                "\n" +
                "Allgemeine Nutzung von Spezl\n" +
                "\n" +
                "Die Nutzungsbedingungen sind als Ergänzung der Nutzungsbedingungen des Google Play Stores zu betrachten. \n" +
                "\n" +
                "Bitte verwende unsere App nicht auf missbräuchliche Art und Weise. Du bist beispielsweise nicht berechtigt auf unsere App zuzugreifen außerhalb der von uns bereitgestellten Oberfläche. Darüber hinaus darf unsere App nur innerhalb der gesetzlichen Rahmenbedingungen der Bundesrepublik Deutschland. Das Team Spezl nimmt sich raus, die Nutzung für jeden einzelnen Nutzer auszusetzen oder einzustellen, wenn Du gegen die Nutzungsrichtlinien verstoßt oder ein mutmaßliches Fehlverhalten vermutet wird.\n" +
                "\n" +
                "Durch die Nutzung unserer App erhältst du keinerlei Urheberechte oder gewerbliche Schutzrechte an unserer App. Du darfst unsere Logo nutzen, für die Veranstaltung eines Events. Dieses Event hat sich an alle lokalen gesetzlichen Vorschriften zu halten. \n" +
                "Wir behalten uns vor, Inhalte deines Profils und deiner erstellten Events auf eine mögliche Rechtswidrigkeit oder Verstöße gegen unsere Richtlinien zu prüfen und gegeben falls rechtliche Schritte einzuleiten. Darüber hinaus können wir Inhalte entfernen oder deren Darstellung ablehnen, wenn diese gegen unsere Richtlinien oder geltendes Recht verstoßen\n" +
                "\n" +
                "Unser Dienst ist auf mobilen Endgeräten verfügbar. Bitte nutze den Dienst nicht in einer Weise, die dich ablenkt und das Einhalten von Verkehrsregeln oder Sicherheitsvorschriften verhindert.\n" +
                "Das Team Spezl nimmt sich raus, die Nutzungsbedingungen jederzeit zu ändern oder anzupassen. Um beispielsweise auf rechtliche Rahmenbedingungen oder Produktänderungen reagieren zu können.\n" +
                "Deine Pflicht ist es die Nutzungsbedingungen daher regelmäßig zu überprüfen. Jegliche Änderungen hinsichtlich einer neuen Funktion sind sofort wirksam. Stimmst Du diesen nicht zu, musst Du den Dienst beenden.\n" +
                "Der Gerichtsstand für sämtliche Streitigkeiten ist Heidenheim an der Brenz.\n" +
                "\n" +
                "Deine Inhalte in unserer App\n" +
                "\n" +
                "In deinem Profil kannst Du persönliche Inhalte einstellen. Du behältst die Rechte als Urheber und alle gewerbliche Schutzrechte an den Inhalten, die Du bei Spezl einstellst. \n" +
                "Indem Du deine Daten in Form von urheberrechtlichen oder sonst rechtlich geschützten Inhalte in unseren Dienst einstellst, räumst du dem Team Spezl und den dazugehörigen Vertragspartnern unentgeltlich die notwendige, nicht ausschließliche, weltweiten und zeitlich unbegrenzten Rechte ein, diese Inhalte ausschließlich zur Erbringung der jeweiligen Leistung innerhalb der App und dem lediglich in dem dafür nötigen Umfang zu verwenden. Damit das Team Spezl den Dienst anbieten kann, müssen deine Inhalte gespeichert werden und auf einem Server gehostet werden. Das Nutzungsrecht umfasst daher das Recht, die Inhalte technisch zu vervielfältigen.Darüber hinaus räumt sich das Team Spezl das Recht der öffentlichen Zugänglichmachung deiner Inhalte, ausschließlich für den Fall, dass Du wegen der Natur des Dienstes (z.B. Veröffentlichung der Teilnehmer eines Events) eine Veröffentlichung beabsichtigen oder Du ausdrücklich einer Zugänglichmachung bestimmt hast. Durch die Teilnahme an einem Event nimmt sich Team Spezl vor, deine Inhalte in Teilen oder in Ganzen in dem dafür notwendig nötigen Teilnehmerkreis zu veröffentlichen.\n" +
                "Das Recht der öffentlichen Zugänglichmachung endet mit der Löschung deiner Daten oder Du die Bestimmung der öffentlichen Zugänglichmachung aufhebst.\n" +
                "\n" +
                "Änderung und Beendigung unseres Spezl-Dienstes\n" +
                "\n" +
                "Unser Bestreben ist es, unseren Dienst laufend zu optimieren und zu verändern. So ist es für uns möglich, unter Berücksichtigung der jeweiligen Interessen Funktionen und Features hinzuzufügen oder zu entfernen. Zudem können bei Bedarf neue Beschränkungen hinzugefügt werden.\n" +
                "Du kannst die Nutzung unseres Dienstes jederzeit beenden.\n" +
                "\n" +
                "Haftung für unseren Dienst\n" +
                "\n" +
                "Bei Vorsatz und grober Fahrlässigkeit, auch der gesetzlichen Vertreter und Erfüllungsgehilfen, haftest Du und das Team Spezl nach den gesetzlichen Bestimmungen. Für das Auftreten von Personen- und Sachschäden sowie im Falle arglistiger Täuschung innerhalb eines Events übernimmt das Team Spezl keine Haftung. Das Team Spezl fungiert hier lediglich als Vermittler verschiedenen Interessensgruppen.\n" +
                "Bei durch Dich oder Team Spezl, deren gesetzlichen Vertreter oder Erfüllungsgehilfen leicht fahrlässig verursachten Sach-und Vermögensschäden ist die Haftung nach den gesetzlichen Anforderungen beschränkt.\n" +
                "\n";

    }
}
