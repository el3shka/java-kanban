package exceptions;

//review - Наверное имелось в виду ManagerFileNotFoundException
//my comms - Необходимо изменить название класса? // В ТЗ ничего про это не сказано.

public class ManagerNotFileFound extends RuntimeException {
    public ManagerNotFileFound(String message) {
        super(message);
    }
}
