package zipgo.pet.exception;

public class PetException extends RuntimeException {

    public PetException(String message) {
        super(message);
    }

    public static class AgeNotFound extends PetException {

        public AgeNotFound() {
            super("분류에 속하지 않는 나이입니다.");
        }

    }

    public static class GenderNotFound extends PetException {

        public GenderNotFound() {
            super("존재하지 않는 성별입니다.");
        }

    }

    public static class BreedsNotFound extends PetException {

        public BreedsNotFound() {
            super("존재하지 않는 견종입니다.");
        }

    }

    public static class PetSizeNotFound extends PetException {

        public PetSizeNotFound() {
            super("존재하지 않는 견종 크기입니다.");
        }

    }

}