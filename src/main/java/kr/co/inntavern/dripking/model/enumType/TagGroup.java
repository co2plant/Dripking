package kr.co.inntavern.dripking.model.enumType;

public enum TagGroup {
    TASTING_AROMA("향 표현"),
    TASTING_PALATE("맛 표현"),
    TASTING_FINISH("여운 표현");

    private final String label;

    TagGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
