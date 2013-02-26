#include "mainwindow.h"
#include <QTabWidget>
#include <QTextEdit>
#include <QLineEdit>
#include <QPlainTextEdit>
#include <QGridLayout>
#include <QLabel>
#include <QRadioButton>
#include <QCheckBox>
#include <QComboBox>
#include <QFontComboBox>
#include <QDateEdit>
#include <QTimeEdit>
#include <QDateTimeEdit>
#include <QSpinBox>
#include <QDoubleSpinBox>
#include <QSlider>
#include <QListWidget>
#include <QTableWidget>

//-----------------------------------------------------------
void MainWindow::_setupUi()
{
    // main window
	setObjectName(QString::fromUtf8("MainWindow"));
	setWindowTitle("SUT GUI Demonstration controlled by QTaste");
	resize(600, 400);

    // main tab widget
    m_uiTabWidget = new QTabWidget(this);
    m_uiTabWidget->setObjectName(QString::fromUtf8("tabWidget"));
    setCentralWidget(m_uiTabWidget);

    // build tab widgets
    _buildDocumentTabWidget();
    _buildChooseTabWidget();
    _buildSelectionTabWidget();
    _buildTableTabWidget();

	QMetaObject::connectSlotsByName(this);
}

//-----------------------------------------------------------
void MainWindow::_buildDocumentTabWidget()
{
    QGridLayout *gridLayout;
    QLabel      *label;

    m_uiDocumentWidget = new QWidget(this);
    m_uiDocumentWidget->setObjectName("documentWidgetTab");
    gridLayout = new QGridLayout(m_uiDocumentWidget);

    label = new QLabel("QLineEdit", m_uiDocumentWidget);
    label->setObjectName("lineEdit_label");
    QLineEdit *lineEdit = new QLineEdit(m_uiDocumentWidget);
    lineEdit->setObjectName("lineEdit");
    lineEdit->setWindowTitle("lineEdit");
    gridLayout->addWidget(label, 0, 0);
    gridLayout->addWidget(lineEdit, 0, 1);

    label = new QLabel("QTextEdit", m_uiDocumentWidget);
    label->setObjectName("textEdit_label");
    QTextEdit *textEdit = new QTextEdit(m_uiDocumentWidget);
    lineEdit->setObjectName("textEdit");
    gridLayout->addWidget(label, 1, 0);
    gridLayout->addWidget(textEdit, 1, 1);

    label = new QLabel("QPlainTextEdit", m_uiDocumentWidget);
    label->setObjectName("plainTextEdit_label");
    QPlainTextEdit *plainTextEdit = new QPlainTextEdit(m_uiDocumentWidget);
    lineEdit->setObjectName("plainTextEdit");
    gridLayout->addWidget(label, 2, 0);
    gridLayout->addWidget(plainTextEdit, 2, 1);

    label = new QLabel("QDateEdit", m_uiDocumentWidget);
    label->setObjectName("dateEdit_label");
    QDateEdit *dateEdit = new QDateEdit(m_uiDocumentWidget);
    lineEdit->setObjectName("dateEdit");
    gridLayout->addWidget(label, 3, 0);
    gridLayout->addWidget(dateEdit, 3, 1);

    label = new QLabel("QTimeEdit", m_uiDocumentWidget);
    label->setObjectName("timeEdit_label");
    QTimeEdit *timeEdit = new QTimeEdit(m_uiDocumentWidget);
    lineEdit->setObjectName("timeEdit");
    gridLayout->addWidget(label, 4, 0);
    gridLayout->addWidget(timeEdit, 4, 1);

    label = new QLabel("QDateTimeEdit", m_uiDocumentWidget);
    label->setObjectName("dateTimeEdit_label");
    QDateTimeEdit *dateTimeEdit = new QDateTimeEdit(m_uiDocumentWidget);
    lineEdit->setObjectName("dateTimeEdit");
    gridLayout->addWidget(label, 5, 0);
    gridLayout->addWidget(dateTimeEdit, 5, 1);

    m_uiTabWidget->addTab(m_uiDocumentWidget, "Document panel");
}

//-----------------------------------------------------------
void MainWindow::_buildChooseTabWidget()
{
    QGridLayout *gridLayout;
    QLabel      *label;

    // build choose tab
    m_uiChooseWidget = new QWidget(this);
    m_uiChooseWidget->setObjectName("chooseWidgetTab");
    gridLayout = new QGridLayout(m_uiChooseWidget);

    label = new QLabel("QRadioButton", m_uiChooseWidget);
    label->setObjectName("radioButton_label");
    QRadioButton *radioButton = new QRadioButton("radio", m_uiChooseWidget);
    radioButton->setObjectName("radioButton");
    gridLayout->addWidget(label, 0, 0);
    gridLayout->addWidget(radioButton, 0, 1);

    label = new QLabel("QCheckBox", m_uiChooseWidget);
    label->setObjectName("checkbox_label");
    QCheckBox *checkbox = new QCheckBox("checkbox", m_uiChooseWidget);
    checkbox->setObjectName("checkbox");
    gridLayout->addWidget(label, 1, 0);
    gridLayout->addWidget(checkbox, 1, 1);

    m_uiTabWidget->addTab(m_uiChooseWidget, "Choose panel");
}

//-----------------------------------------------------------
void MainWindow::_buildSelectionTabWidget()
{
    QGridLayout *gridLayout;
    QLabel      *label;

    // build selection tab
    m_uiSelectionWidget = new QWidget(this);
    m_uiSelectionWidget->setObjectName("selectionWidgetTab");
    gridLayout = new QGridLayout(m_uiSelectionWidget);

    label = new QLabel("QListWidget", m_uiSelectionWidget);
    label->setObjectName("listWidget_label");
    gridLayout->addWidget(label, 0, 0);
    QListWidget *listWidget= new QListWidget(m_uiSelectionWidget);
    listWidget->setObjectName("listWidget");
    listWidget->addItem("listitem_01");
    listWidget->addItem("listitem_02");
    listWidget->addItem("listitem_03");
    listWidget->addItem("listitem_04");
    gridLayout->addWidget(listWidget, 0, 1);

    label = new QLabel("QComboBox", m_uiSelectionWidget);
    label->setObjectName("combox_label");
    gridLayout->addWidget(label, 1, 0);
    QComboBox *comboBox = new QComboBox(m_uiSelectionWidget);
    comboBox->setObjectName("comboBox");
    comboBox->addItem("elmt_01");
    comboBox->addItem("elmt_02");
    comboBox->addItem("elmt_03");
    comboBox->addItem("elmt_04");
    gridLayout->addWidget(comboBox, 1, 1);

    label = new QLabel("QFontComboBox", m_uiSelectionWidget);
    label->setObjectName("fontComboBox_label");
    QFontComboBox *fontComboBox = new QFontComboBox(m_uiSelectionWidget);
    fontComboBox->setObjectName("fontComboBox");
    gridLayout->addWidget(label, 2, 0);
    gridLayout->addWidget(fontComboBox, 2, 1);

    label = new QLabel("QSpinBox", m_uiSelectionWidget);
    label->setObjectName("spinBox_label");
    QSpinBox *spinBox = new QSpinBox(m_uiSelectionWidget);
    spinBox->setObjectName("spinBox");
    gridLayout->addWidget(label, 3, 0);
    gridLayout->addWidget(spinBox, 3, 1);

    label = new QLabel("QDoubleSpinBox", m_uiSelectionWidget);
    label->setObjectName("doubleSpinBox_label");
    QDoubleSpinBox *doubleSpinBox = new QDoubleSpinBox(m_uiSelectionWidget);
    doubleSpinBox->setObjectName("doubleSpinBox");
    gridLayout->addWidget(label, 4, 0);
    gridLayout->addWidget(doubleSpinBox, 4, 1);

    label = new QLabel("QSlider", m_uiSelectionWidget);
    label->setObjectName("slider_label");
    QSlider *slider = new QSlider(Qt::Horizontal, m_uiSelectionWidget);
    slider->setObjectName("slider");
    gridLayout->addWidget(label, 5, 0);
    gridLayout->addWidget(slider, 5, 1);

    m_uiTabWidget->addTab(m_uiSelectionWidget, "Selection panel");
}

//-----------------------------------------------------------
void MainWindow::_buildTableTabWidget()
{
    QGridLayout *gridLayout;
    QLabel      *label;

    // build table tab
    m_uiTableWidget = new QWidget(this);
    m_uiTableWidget->setObjectName("tableWidgetTab");
    gridLayout = new QGridLayout(m_uiTableWidget);

    label = new QLabel("QTableWidget", m_uiTableWidget);
    label->setObjectName("tableWidget_label");
    gridLayout->addWidget(label, 0, 0);
    QTableWidget *tableWidget = new QTableWidget(5, 2, m_uiTableWidget);
    tableWidget->setObjectName("tableWidget");
    tableWidget->setItem(0, 0, new QTableWidgetItem("1"));
    tableWidget->setItem(0, 1, new QTableWidgetItem("tableitem_01"));
    tableWidget->setItem(1, 0, new QTableWidgetItem("2"));
    tableWidget->setItem(1, 1, new QTableWidgetItem("tableitem_02"));
    tableWidget->setItem(2, 0, new QTableWidgetItem("3"));
    tableWidget->setItem(2, 1, new QTableWidgetItem("tableitem_03"));
    tableWidget->setItem(3, 0, new QTableWidgetItem("4"));
    tableWidget->setItem(3, 1, new QTableWidgetItem("tableitem_04"));
    tableWidget->setItem(4, 0, new QTableWidgetItem("5"));
    tableWidget->setItem(4, 1, new QTableWidgetItem("tableitem_05"));
    gridLayout->addWidget(tableWidget, 0, 1);

    m_uiTabWidget->addTab(m_uiTableWidget, "Table panel");
}

//-----------------------------------------------------------
MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent)
{
    _setupUi();
}

//-----------------------------------------------------------
MainWindow::~MainWindow()
{
}
