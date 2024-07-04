import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ContactManager extends JFrame {
    private ArrayList<Contact> contacts;
    private DefaultTableModel tableModel;
    private JTable table;

    public ContactManager() {
        contacts = loadContacts();
        setTitle("Contact Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout setup
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Name", "Phone Number", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load contacts into table
        loadContactsToTable();

        // Button actions
        addButton.addActionListener(e -> addContact());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());

        setVisible(true);
    }

    private void addContact() {
        ContactDialog dialog = new ContactDialog(this, "Add Contact", null);
        Contact contact = dialog.getContact();
        if (contact != null) {
            contacts.add(contact);
            saveContacts();
            loadContactsToTable();
        }
    }

    private void editContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Contact contact = contacts.get(selectedRow);
            ContactDialog dialog = new ContactDialog(this, "Edit Contact", contact);
            Contact editedContact = dialog.getContact();
            if (editedContact != null) {
                contacts.set(selectedRow, editedContact);
                saveContacts();
                loadContactsToTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to edit.");
        }
    }

    private void deleteContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            contacts.remove(selectedRow);
            saveContacts();
            loadContactsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
        }
    }

    private void loadContactsToTable() {
        tableModel.setRowCount(0);  // Clear existing data
        for (Contact contact : contacts) {
            tableModel.addRow(new Object[]{contact.getName(), contact.getPhoneNumber(), contact.getEmail()});
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Contact> loadContacts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("contacts.dat"))) {
            return (ArrayList<Contact>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("contacts.dat"))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ContactManager::new);
    }
}

class ContactDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private Contact contact;

    public ContactDialog(JFrame parent, String title, Contact contact) {
        super(parent, title, true);
        this.contact = contact;

        setLayout(new GridLayout(4, 2, 10, 10));
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        JButton saveButton = new JButton("Save");
        add(saveButton);

        saveButton.addActionListener(e -> {
            this.contact = new Contact(nameField.getText(), phoneField.getText(), emailField.getText());
            dispose();
        });

        if (contact != null) {
            nameField.setText(contact.getName());
            phoneField.setText(contact.getPhoneNumber());
            emailField.setText(contact.getEmail());
        }

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public Contact getContact() {
        return contact;
    }
}
