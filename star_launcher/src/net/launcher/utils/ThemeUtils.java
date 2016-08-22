package net.launcher.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

import net.launcher.components.Frame;
import net.launcher.components.LinkLabel;
import net.launcher.theme.DraggerTheme;
import net.launcher.theme.LoginTheme;
import net.launcher.theme.OptionsTheme;
import net.launcher.theme.PersonalTheme;
import net.launcher.theme.RegTheme;

public class ThemeUtils extends BaseUtils {

   public static void updateStyle(Frame main) throws Exception {
      int i = 0;
      LinkLabel[] var2 = main.links;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LinkLabel link = var2[var4];
//        LoginTheme.links.apply(link);		// закомментированы ссылки
         FontMetrics fm = link.getFontMetrics(link.getFont());
//       link.setBounds(i + LoginTheme.links.x, LoginTheme.links.y, fm.stringWidth(link.getText()), fm.getHeight());
//         i += fm.stringWidth(link.getText()) + LoginTheme.links.margin;
      }

      DraggerTheme.title.apply(main.title);
      DraggerTheme.dragger.apply(main.dragger);
      DraggerTheme.dbuttons.apply(main.hide, main.close);
      LoginTheme.toGame.apply(Frame.toGame);
      LoginTheme.toAuth.apply(Frame.toAuth);
      LoginTheme.toLogout.apply(Frame.toLogout);
      LoginTheme.toPersonal.apply(Frame.toPersonal);
      LoginTheme.toRegister.apply(Frame.toRegister);
      LoginTheme.toOptions.apply(main.toOptions);
      LoginTheme.login.apply(Frame.login);
      LoginTheme.password.apply(Frame.password);
      LoginTheme.servers.apply(main.servers);
      LoginTheme.serverbar.apply(main.serverbar);
      OptionsTheme.loadnews.apply(main.loadnews);
      OptionsTheme.Music.apply(main.Music);
      OptionsTheme.updatepr.apply(main.updatepr);
      OptionsTheme.cleandir.apply(main.cleanDir);
      OptionsTheme.fullscrn.apply(main.fullscreen);
      OptionsTheme.memory.apply(main.memory);
      OptionsTheme.drive.apply(main.drive);
      OptionsTheme.close.apply(main.options_close);
      OptionsTheme.folderSelector.apply(main.folder_select);
      
      RegTheme.closereg.apply(main.closereg);
      RegTheme.loginReg.apply(main.loginReg);
      RegTheme.passwordReg.apply(main.passwordReg);
      RegTheme.password2Reg.apply(main.password2Reg);
      RegTheme.mailReg.apply(main.mailReg);
      RegTheme.okreg.apply(main.okreg);
      PersonalTheme.buyCloak.apply(main.buyCloak);
      PersonalTheme.changeskin.apply(main.changeSkin);
      PersonalTheme.buyVip.apply(main.buyVip);
      PersonalTheme.buyPremium.apply(main.buyPremium);
      PersonalTheme.buyUnban.apply(main.buyUnban);
      PersonalTheme.vaucher.apply(main.vaucher);
      PersonalTheme.vaucherButton.apply(main.vaucherButton);
      PersonalTheme.buyVaucher.apply(main.buyVaucher);
      PersonalTheme.exchangeFrom.apply(main.exchangeFrom);
      PersonalTheme.exchangeTo.apply(main.exchangeTo);
      PersonalTheme.exchangeBtn.apply(main.exchangeButton);
      PersonalTheme.toGamePSL.apply(main.toGamePersonal);
      LoginTheme.update_no.apply(main.update_no);
      LoginTheme.update_exe.apply(main.update_exe);
      LoginTheme.update_jar.apply(main.update_jar);
      LoginTheme.newsBrowser.apply(main.bpane);
      main.panel.setPreferredSize(new Dimension(LoginTheme.frameW, LoginTheme.frameH));
      main.setIconImage(BaseUtils.getLocalImage("favicon"));
      main.setTitle("");
      main.setLocationRelativeTo((Component)null);
      main.pack();
      main.repaint();
   }
}
