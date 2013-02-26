#include <QApplication>
#include "mainwindow.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    a.setApplicationName("qtapp");
    a.setApplicationVersion("1.0");
    MainWindow w;
    w.show();
    
    return a.exec();
}
