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

    m_uiDocumentWidget = new QWidget(this);
    gridLayout = new QGridLayout(m_uiDocumentWidget);

    gridLayout->addWidget(new QLabel("QLineEdit", m_uiDocumentWidget), 0, 0);
    gridLayout->addWidget(new QLineEdit(m_uiDocumentWidget), 0, 1);

    gridLayout->addWidget(new QLabel("QTextEdit", m_uiDocumentWidget), 1, 0);
    gridLayout->addWidget(new QTextEdit(m_uiDocumentWidget), 1, 1);

    gridLayout->addWidget(new QLabel("QPlainTextEdit", m_uiDocumentWidget), 2, 0);
    gridLayout->addWidget(new QPlainTextEdit(m_uiDocumentWidget), 2, 1);

    gridLayout->addWidget(new QLabel("QDateEdit", m_uiDocumentWidget), 3, 0);
    gridLayout->addWidget(new QDateEdit(m_uiDocumentWidget), 3, 1);

    gridLayout->addWidget(new QLabel("QTimeEdit", m_uiDocumentWidget), 4, 0);
    gridLayout->addWidget(new QTimeEdit(m_uiDocumentWidget), 4, 1);

    gridLayout->addWidget(new QLabel("QDateTimeEdit", m_uiDocumentWidget), 5, 0);
    gridLayout->addWidget(new QDateTimeEdit(m_uiDocumentWidget), 5, 1);

    m_uiTabWidget->addTab(m_uiDocumentWidget, "Document panel");
}

//-----------------------------------------------------------
void MainWindow::_buildChooseTabWidget()
{
    QGridLayout *gridLayout;

    // build choose tab
    m_uiChooseWidget = new QWidget(this);
    gridLayout = new QGridLayout(m_uiChooseWidget);

    gridLayout->addWidget(new QLabel("QRadioButton", m_uiChooseWidget), 0, 0);
    gridLayout->addWidget(new QRadioButton("radio", m_uiChooseWidget), 0, 1);

    gridLayout->addWidget(new QLabel("QCheckBox", m_uiChooseWidget), 1, 0);
    gridLayout->addWidget(new QCheckBox("checkbox", m_uiChooseWidget), 1, 1);

    m_uiTabWidget->addTab(m_uiChooseWidget, "Choose panel");
}

//-----------------------------------------------------------
void MainWindow::_buildSelectionTabWidget()
{
    QGridLayout *gridLayout;

    // build selection tab
    m_uiSelectionWidget = new QWidget(this);
    gridLayout = new QGridLayout(m_uiSelectionWidget);

    gridLayout->addWidget(new QLabel("QListWidget", m_uiSelectionWidget), 0, 0);
    QListWidget *listWidget= new QListWidget(m_uiSelectionWidget);
    listWidget->addItem("listitem_01");
    listWidget->addItem("listitem_02");
    listWidget->addItem("listitem_03");
    listWidget->addItem("listitem_04");
    gridLayout->addWidget(listWidget, 0, 1);

    gridLayout->addWidget(new QLabel("QComboBox", m_uiSelectionWidget), 1, 0);
    QComboBox *comboBox = new QComboBox(m_uiSelectionWidget);
    comboBox->addItem("elmt_01");
    comboBox->addItem("elmt_02");
    comboBox->addItem("elmt_03");
    comboBox->addItem("elmt_04");
    gridLayout->addWidget(comboBox, 1, 1);

    gridLayout->addWidget(new QLabel("QFontComboBox", m_uiSelectionWidget), 2, 0);
    gridLayout->addWidget(new QFontComboBox(m_uiSelectionWidget), 2, 1);

    gridLayout->addWidget(new QLabel("QSpinBox", m_uiSelectionWidget), 3, 0);
    gridLayout->addWidget(new QSpinBox(m_uiSelectionWidget), 3, 1);

    gridLayout->addWidget(new QLabel("QDoubleSpinBox", m_uiSelectionWidget), 4, 0);
    gridLayout->addWidget(new QDoubleSpinBox(m_uiSelectionWidget), 4, 1);

    gridLayout->addWidget(new QLabel("QSlider", m_uiSelectionWidget), 5, 0);
    gridLayout->addWidget(new QSlider(Qt::Horizontal, m_uiSelectionWidget), 5, 1);

    m_uiTabWidget->addTab(m_uiSelectionWidget, "Selection panel");
}

//-----------------------------------------------------------
void MainWindow::_buildTableTabWidget()
{
    QGridLayout *gridLayout;

    // build table tab
    m_uiTableWidget = new QWidget(this);
    gridLayout = new QGridLayout(m_uiTableWidget);

    gridLayout->addWidget(new QLabel("QTableWidget", m_uiTableWidget), 0, 0);
    QTableWidget *tableWidget = new QTableWidget(5, 2, m_uiTableWidget);
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
