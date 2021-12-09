package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Funcionario;
import model.exceptions.ValidationException;
import model.services.FuncionarioService;

public class FuncionarioFormController implements Initializable {

	private Funcionario entity;
	private FuncionarioService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtSobrenome;

	@FXML
	private Label lbError;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	public void setFuncionario(Funcionario entity) {
		this.entity = entity;
	}

	public void setFuncionarioService(FuncionarioService service) {
		this.service = service;
	}

	public void subscribeDataChangeListers(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade null");
		}
		if (service == null) {
			throw new IllegalStateException("Serviço null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeLiteners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro em salvar o objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeLiteners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Funcionario getFormData() {
		Funcionario obj = new Funcionario();

		ValidationException exception = new ValidationException("Erro de validação");

		obj.setId(Utils.tryParserToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Falha, o campo não pode ser vazio");
		}

		obj.setNome(txtNome.getText());
		
		if (txtSobrenome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("sobrenome", "Falha, o campo não pode ser vazio");
		}

		obj.setSobrenome(txtSobrenome.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
		Constraints.setTextFieldMaxLength(txtSobrenome, 30);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtSobrenome.setText(entity.getSobrenome());
	}

	@SuppressWarnings("unused")
	private void setErrorMessages(Map<String, String> error) {
		Set<String> fields = error.keySet();

		if (fields.contains("nome")) {
			lbError.setText(error.get("nome"));
		}
		
		if (fields.contains("sobrenome")) {
			lbError.setText(error.get("sobrenome"));
		}
	}

}
