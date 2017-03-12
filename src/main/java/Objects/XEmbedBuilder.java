package Objects;

import Main.Utility;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class XEmbedBuilder extends EmbedBuilder {

    @Override
    public EmbedBuilder withTitle(String title) {
        return super.withTitle(Utility.removeMentions(title));
    }

    @Override
    public EmbedBuilder withDesc(String desc) {
        return super.withDesc(Utility.removeMentions(desc));
    }

    @Override
    public EmbedBuilder withDescription(String desc){
        return super.withDescription(Utility.removeMentions(desc));
    }

    @Override
    public EmbedBuilder appendField(String title, String content, boolean inline) {
        return super.appendField(Utility.removeMentions(title), Utility.removeMentions(content), inline);
    }

    @Override
    public EmbedBuilder withFooterText(String footerText) {
        return super.withFooterText(Utility.removeMentions(footerText));
    }

    @Override
    public EmbedBuilder withAuthorName(String authorName) {
        return super.withAuthorName(Utility.removeMentions(authorName));
    }
}
