#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

class QTabWidget;

class MainWindow : public QMainWindow
{
    Q_OBJECT

private:
    QTabWidget	*m_uiTabWidget;
    QWidget     *m_uiDocumentWidget;
    QWidget     *m_uiChooseWidget;
    QWidget     *m_uiSelectionWidget;
    QWidget     *m_uiTableWidget;

private:
	void _setupUi();
    void _buildDocumentTabWidget();
    void _buildChooseTabWidget();
    void _buildSelectionTabWidget();
    void _buildTableTabWidget();
    
public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();
};

#endif // MAINWINDOW_H
