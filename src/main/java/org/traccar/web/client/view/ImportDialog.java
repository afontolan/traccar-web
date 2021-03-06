/*
 * Copyright 2014 Vitaly Litvak (vitavaque@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.web.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import org.traccar.web.client.i18n.Messages;
import org.traccar.web.client.model.DeviceProperties;
import org.traccar.web.shared.model.Device;

public class ImportDialog {
    private static ImportDialogUiBinder uiBinder = GWT.create(ImportDialogUiBinder.class);

    interface ImportDialogUiBinder extends UiBinder<Widget, ImportDialog> {
    }

    @UiField
    Window window;

    @UiField
    FormPanel form;

    @UiField
    FileUploadField fileToImport;

    @UiField(provided = true)
    Messages i18n = GWT.create(Messages.class);

    @UiField(provided = true)
    ComboBox<Device> deviceCombo;

    public ImportDialog(final ListStore<Device> deviceStore) {
        DeviceProperties deviceProperties = GWT.create(DeviceProperties.class);
        this.deviceCombo = new ComboBox<>(deviceStore, deviceProperties.label());
        uiBinder.createAndBindUi(this);
    }

    public void show() {
        window.show();
    }

    public void hide() {
        window.hide();
    }

    @UiHandler("saveButton")
    public void onImportClicked(SelectEvent event) {
        if (deviceCombo.getValue() == null) {
            new AlertMessageBox(i18n.error(), i18n.errFillFields()).show();
        } else {
            final AutoProgressMessageBox messageBox = new AutoProgressMessageBox(window.getTitle(), i18n.importingData());
            messageBox.auto();
            messageBox.show();

            form.addSubmitCompleteHandler(new SubmitCompleteEvent.SubmitCompleteHandler() {
                @Override
                public void onSubmitComplete(SubmitCompleteEvent event) {
                    window.hide();
                    messageBox.hide();
                    new LogViewDialog(event.getResults()).show();
                }
            });
            form.setAction(form.getAction() + "?deviceId=" + deviceCombo.getValue().getId());
            form.submit();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(SelectEvent event) {
        window.hide();
    }

}
