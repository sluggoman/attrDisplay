import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import java.awt.Color
import javax.swing.*
import static liveplugin.PluginUtil.*

def getContents = { event ->
    def editor = event.getRequiredData(CommonDataKeys.EDITOR)
    def dc = event.getDataContext()
    def vf = dc.getData("virtualFile")
    def url = editor.getProject().getBaseDir().toString() +
             "/attr/" + vf.getName().replace(".java", ".in")
    def vfm = VirtualFileManager.getInstance()
    def vfin = vfm.findFileByUrl(url)
    new String(vfin.contentsToByteArray())
}

registerAction("DisplayAttrAction", "alt shift D") { AnActionEvent event ->
    def editor = event.getRequiredData(CommonDataKeys.EDITOR)
    def relpoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor)
    def bb = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(getContents(event), null, new Color(186, 238, 186), null)
    def balloon = bb.createBalloon()
    balloon.show(relpoint, Balloon.Position.atRight)
}

registerAction("ToolWindowAttrAction", "alt shift W") { AnActionEvent event ->
    def editor = event.getRequiredData(CommonDataKeys.EDITOR)
    def tw = ToolWindowManager.getInstance(editor.getProject()).getToolWindow("DB Attributes")
    def contentManager = tw.getContentManager()
    contentManager.removeAllContents(false);
    JComponent tc = new JEditorPane()
    tc.setContentType("text/html")
    tc.setText(getContents(event))
    def dc = event.getDataContext()
    def vf = dc.getData("virtualFile")
    def content = contentManager.getFactory().createContent(tc, vf.getName(), false)
    contentManager.addContent(content)
}

registerToolWindow("DB Attributes", pluginDisposable) {
    def panel = new JPanel()
    panel
}
