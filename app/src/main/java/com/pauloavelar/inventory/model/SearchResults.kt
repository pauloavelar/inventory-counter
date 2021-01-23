package com.pauloavelar.inventory.model

data class SearchResults(
        val query: String?,
        val matches: Int,
        val totalItems: Int,
        val results: List<InventoryItem>
) {
    companion object {
        val EMPTY = SearchResults(
                query = null,
                matches = 0,
                totalItems = 0,
                results = emptyList()
        )
    }
}
