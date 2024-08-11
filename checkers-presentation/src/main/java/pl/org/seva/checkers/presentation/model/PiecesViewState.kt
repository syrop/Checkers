package pl.org.seva.checkers.presentation.model

data class PiecesViewState(
    val isLoading: Boolean = false,
    val pieces: PiecesPresentationModel = PiecesPresentationModel(
        emptySet(),
        emptySet(),
        emptySet(),
        emptySet(),
    )
) {
    fun loading() = copy(isLoading = true)

    fun dishDetailsReady(
        pieces: PiecesPresentationModel,
    ) = copy(pieces = pieces, isLoading = false)

}
