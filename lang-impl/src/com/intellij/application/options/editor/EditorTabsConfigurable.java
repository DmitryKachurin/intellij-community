package com.intellij.application.options.editor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.ide.ui.UISettings;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;

/**
 * @author yole
 */
public class EditorTabsConfigurable implements EditorOptionsProvider {
  private JPanel myRootPanel;
  private JCheckBox myHideKnownExtensions;
  private JCheckBox myScrollTabLayoutInEditorCheckBox;
  private JTextField myEditorTabLimitField;
  private JComboBox myEditorTabPlacement;
  private JRadioButton myCloseNonModifiedFilesFirstRadio;
  private JRadioButton myCloseLRUFilesRadio;
  private JRadioButton myActivateLeftEditorOnCloseRadio;
  private JRadioButton myActivateMRUEditorOnCloseRadio;
  private JCheckBox myCbModifiedTabsMarkedWithAsterisk;
  private JCheckBox myShowCloseButtonOnCheckBox;

  public EditorTabsConfigurable() {
    myEditorTabPlacement.setModel(new DefaultComboBoxModel(new Object[]{
      SwingConstants.TOP,
      SwingConstants.LEFT,
      SwingConstants.BOTTOM,
      SwingConstants.RIGHT,
      UISettings.TABS_NONE,
    }));
    myEditorTabPlacement.setRenderer(new MyTabsPlacementComboBoxRenderer());
  }

  @Nls
  public String getDisplayName() {
    return "Editor Tabs";
  }

  public Icon getIcon() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    return myRootPanel;
  }

  public void reset() {
    UISettings uiSettings=UISettings.getInstance();

    myCbModifiedTabsMarkedWithAsterisk.setSelected(uiSettings.MARK_MODIFIED_TABS_WITH_ASTERISK);
    myScrollTabLayoutInEditorCheckBox.setSelected(uiSettings.SCROLL_TAB_LAYOUT_IN_EDITOR);
    myEditorTabPlacement.setSelectedItem(uiSettings.EDITOR_TAB_PLACEMENT);
    myHideKnownExtensions.setSelected(uiSettings.HIDE_KNOWN_EXTENSION_IN_TABS);
    myEditorTabLimitField.setText(Integer.toString(uiSettings.EDITOR_TAB_LIMIT));
    myShowCloseButtonOnCheckBox.setSelected(uiSettings.SHOW_CLOSE_BUTTON);

    if (uiSettings.CLOSE_NON_MODIFIED_FILES_FIRST) {
      myCloseNonModifiedFilesFirstRadio.setSelected(true);
    }
    else {
      myCloseLRUFilesRadio.setSelected(true);
    }
    if (uiSettings.ACTIVATE_MRU_EDITOR_ON_CLOSE) {
      myActivateMRUEditorOnCloseRadio.setSelected(true);
    }
    else {
      myActivateLeftEditorOnCloseRadio.setSelected(true);
    }
  }

  public void apply() throws ConfigurationException {
    UISettings uiSettings=UISettings.getInstance();

    boolean uiSettingsChanged = uiSettings.MARK_MODIFIED_TABS_WITH_ASTERISK != myCbModifiedTabsMarkedWithAsterisk.isSelected();
    uiSettings.MARK_MODIFIED_TABS_WITH_ASTERISK = myCbModifiedTabsMarkedWithAsterisk.isSelected();

    if (isModified(myScrollTabLayoutInEditorCheckBox, uiSettings.SCROLL_TAB_LAYOUT_IN_EDITOR)) uiSettingsChanged = true;
    uiSettings.SCROLL_TAB_LAYOUT_IN_EDITOR = myScrollTabLayoutInEditorCheckBox.isSelected();

    if (isModified(myShowCloseButtonOnCheckBox, uiSettings.SHOW_CLOSE_BUTTON)) uiSettingsChanged = true;
    uiSettings.SHOW_CLOSE_BUTTON = myShowCloseButtonOnCheckBox.isSelected();

    final int tabPlacement = ((Integer)myEditorTabPlacement.getSelectedItem()).intValue();
    if (uiSettings.EDITOR_TAB_PLACEMENT != tabPlacement) uiSettingsChanged = true;
    uiSettings.EDITOR_TAB_PLACEMENT = tabPlacement;

    boolean hide = myHideKnownExtensions.isSelected();
    if (uiSettings.HIDE_KNOWN_EXTENSION_IN_TABS != hide) uiSettingsChanged = true;
    uiSettings.HIDE_KNOWN_EXTENSION_IN_TABS = hide;

    uiSettings.CLOSE_NON_MODIFIED_FILES_FIRST = myCloseNonModifiedFilesFirstRadio.isSelected();
    uiSettings.ACTIVATE_MRU_EDITOR_ON_CLOSE = myActivateMRUEditorOnCloseRadio.isSelected();

    String temp = myEditorTabLimitField.getText();
    if(temp.trim().length() > 0){
      try {
        int newEditorTabLimit = Integer.parseInt(temp);
        if(newEditorTabLimit>0&&newEditorTabLimit!=uiSettings.EDITOR_TAB_LIMIT){
          uiSettings.EDITOR_TAB_LIMIT=newEditorTabLimit;
          uiSettingsChanged = true;
        }
      }catch (NumberFormatException ignored){}
    }
    if(uiSettingsChanged){
      uiSettings.fireUISettingsChanged();
    }
  }

  public boolean isModified() {
    final UISettings uiSettings = UISettings.getInstance();
    boolean isModified = isModified(myCbModifiedTabsMarkedWithAsterisk, uiSettings.MARK_MODIFIED_TABS_WITH_ASTERISK);;
    isModified |= isModified(myEditorTabLimitField, uiSettings.EDITOR_TAB_LIMIT);
    int tabPlacement = ((Integer)myEditorTabPlacement.getSelectedItem()).intValue();
    isModified |= tabPlacement != uiSettings.EDITOR_TAB_PLACEMENT;
    isModified |= myHideKnownExtensions.isSelected() != uiSettings.HIDE_KNOWN_EXTENSION_IN_TABS;

    isModified |= myScrollTabLayoutInEditorCheckBox.isSelected() != uiSettings.SCROLL_TAB_LAYOUT_IN_EDITOR;
    isModified |= myShowCloseButtonOnCheckBox.isSelected() != uiSettings.SHOW_CLOSE_BUTTON;

    isModified |= isModified(myCloseNonModifiedFilesFirstRadio, uiSettings.CLOSE_NON_MODIFIED_FILES_FIRST);
    isModified |= isModified(myActivateMRUEditorOnCloseRadio, uiSettings.ACTIVATE_MRU_EDITOR_ON_CLOSE);

    return isModified;
  }

  public void disposeUIResources() {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  private static boolean isModified(JToggleButton checkBox, boolean value) {
    return checkBox.isSelected() != value;
  }

  private static boolean isModified(JTextField textField, int value) {
    try {
      int fieldValue = Integer.parseInt(textField.getText().trim());
      return fieldValue != value;
    }
    catch(NumberFormatException e) {
      return false;
    }
  }

  private static final class MyTabsPlacementComboBoxRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
      int tabPlacement = ((Integer)value).intValue();
      String text;
      if (UISettings.TABS_NONE == tabPlacement) {
        text = ApplicationBundle.message("combobox.tab.placement.none");
      }
      else if (SwingConstants.TOP == tabPlacement) {
        text = ApplicationBundle.message("combobox.tab.placement.top");
      }
      else if (SwingConstants.LEFT == tabPlacement) {
        text = ApplicationBundle.message("combobox.tab.placement.left");
      }
      else if (SwingConstants.BOTTOM == tabPlacement) {
        text = ApplicationBundle.message("combobox.tab.placement.bottom");
      }
      else if (SwingConstants.RIGHT == tabPlacement) {
        text = ApplicationBundle.message("combobox.tab.placement.right");
      }
      else {
        throw new IllegalArgumentException("unknown tabPlacement: " + tabPlacement);
      }
      return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }
  }

  public String getId() {
    return "editor.preferences.tabs";
  }

  public Runnable enableSearch(final String option) {
    return null;
  }
}
