package io.dev.taxi.observables

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EditTextObservable {
    fun from(input: EditText): Observable<String> {
        val subject = PublishSubject.create<String>()
        input.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                subject.onNext(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        return subject
    }
}
